package openperipheral.interfaces.oc.providers;

import java.util.Map;

import li.cil.oc.api.network.ManagedEnvironment;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import openmods.Log;
import openperipheral.adapter.TileEntityBlacklist;
import openperipheral.interfaces.oc.ModuleOpenComputers;

import com.google.common.collect.Maps;

public class DriverOpenPeripheral implements li.cil.oc.api.driver.Block {

	private final Map<Class<?>, Boolean> cache = Maps.newHashMap();

	@Override
	public boolean worksWith(World world, BlockPos pos) {
		final TileEntity te = world.getTileEntity(pos);
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
	public ManagedEnvironment createEnvironment(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te == null) {
			Log.warn("Trying to provide environment for %s in world %d, but TE not found", pos, world.provider.getDimensionId());
			return null;
		}

		final IEnviromentInstanceWrapper<ManagedEnvironment> adaptedClass = ModuleOpenComputers.PERIPHERAL_METHODS_FACTORY.getAdaptedClass(te.getClass());
		return adaptedClass.createEnvironment(te);
	}

}
