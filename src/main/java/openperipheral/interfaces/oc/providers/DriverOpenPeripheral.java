package openperipheral.interfaces.oc.providers;

import com.google.common.collect.Maps;
import java.util.Map;
import li.cil.oc.api.network.ManagedEnvironment;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmods.Log;
import openperipheral.adapter.TileEntityBlacklist;
import openperipheral.interfaces.oc.ModuleOpenComputers;

public class DriverOpenPeripheral implements li.cil.oc.api.driver.Block {

	private final Map<Class<?>, Boolean> cache = Maps.newHashMap();

	@Override
	public boolean worksWith(World world, int x, int y, int z) {
		final TileEntity te = world.getTileEntity(x, y, z);
		if (te == null) return false;

		final Class<?> cls = te.getClass();

		Boolean result = cache.get(cls);

		if (result == null) {
			result = shouldProvide(cls);
			cache.put(cls, result);
		}

		return result;
	}

	private static boolean shouldProvide(Class<?> cls) {
		if (TileEntityBlacklist.INSTANCE.isBlacklisted(cls)) return false;
		IEnviromentInstanceWrapper<?> factory = ModuleOpenComputers.PERIPHERAL_METHODS_FACTORY.getAdaptedClass(cls);
		return !factory.isEmpty();
	}

	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te == null) {
			Log.warn("Trying to provide environment for %d,%d,%d in world %d, but TE not found", x, y, z, world.provider.dimensionId);
			return null;
		}

		final IEnviromentInstanceWrapper<ManagedEnvironment> adaptedClass = ModuleOpenComputers.PERIPHERAL_METHODS_FACTORY.getAdaptedClass(te.getClass());
		return adaptedClass.createEnvironment(te);
	}

}
