package openperipheral.adapter;

import java.util.*;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmods.Log;
import openperipheral.Config;
import openperipheral.adapter.composed.ClassMethodsList;
import openperipheral.adapter.peripheral.AdapterPeripheral;
import openperipheral.adapter.peripheral.IPeripheralMethodExecutor;
import openperipheral.api.ICustomPeripheralProvider;
import openperipheral.api.Volatile;
import openperipheral.util.PeripheralUtils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

public class PeripheralHandlers implements IPeripheralProvider {
	private static final IPeripheralFactory<TileEntity> ADAPTER_HANDLER = new SafePeripheralFactory() {
		@Override
		protected IPeripheral createPeripheral(TileEntity tile, int side) {
			ClassMethodsList<IPeripheralMethodExecutor> adapter = AdapterManager.peripherals.adaptClass(tile.getClass());
			return new AdapterPeripheral(adapter, tile);
		}
	};

	private static final IPeripheralFactory<TileEntity> ADAPTER_CACHING_HANDLER = new CachingPeripheralFactory() {
		@Override
		protected IPeripheral createPeripheral(TileEntity tile, int side) {
			ClassMethodsList<IPeripheralMethodExecutor> adapter = AdapterManager.peripherals.adaptClass(tile.getClass());
			return new AdapterPeripheral(adapter, tile);
		}
	};

	private static final IPeripheralFactory<TileEntity> PROVIDER_HANDLER = new SafePeripheralFactory() {
		@Override
		protected IPeripheral createPeripheral(TileEntity tile, int side) {
			return (tile instanceof ICustomPeripheralProvider)? ((ICustomPeripheralProvider)tile).createPeripheral(side) : null;
		}
	};

	private static final IPeripheralFactory<TileEntity> PROVIDER_CACHING_HANDLER = new CachingPeripheralFactory() {
		@Override
		protected IPeripheral createPeripheral(TileEntity tile, int side) {
			return (tile instanceof ICustomPeripheralProvider)? ((ICustomPeripheralProvider)tile).createPeripheral(side) : null;
		}
	};

	private static final Map<Class<? extends TileEntity>, IPeripheralFactory<TileEntity>> adaptedClasses = Maps.newHashMap();

	public static Collection<Class<? extends TileEntity>> getAllAdaptedTeClasses() {
		return Collections.unmodifiableCollection(adaptedClasses.keySet());
	}

	@SuppressWarnings("unchecked")
	public static void discoverPeripherals() {
		Map<Class<? extends TileEntity>, String> classToNameMap = PeripheralUtils.getClassToNameMap();

		Set<Class<? extends TileEntity>> candidates = Sets.newHashSet();
		Set<Class<? extends TileEntity>> providerClasses = Sets.newHashSet();
		Set<String> blacklist = ImmutableSet.copyOf(Config.teBlacklist);

		for (Map.Entry<Class<? extends TileEntity>, String> e : classToNameMap.entrySet()) {
			final Class<? extends TileEntity> teClass = e.getKey();
			final String name = e.getValue();

			if (teClass == null) {
				Log.warn("TE with id %s has null key", name);
			} else if (blacklist.contains(teClass.getName()) || blacklist.contains(name.toLowerCase())) {
				Log.warn("Ignoring blacklisted TE %s = %s", name, teClass);
			} else if (ICustomPeripheralProvider.class.isAssignableFrom(teClass)) {
				providerClasses.add(teClass);
			} else if (!IPeripheral.class.isAssignableFrom(teClass)) {
				candidates.add(teClass);
			}

		}

		Set<Class<? extends TileEntity>> interestingClasses = Sets.newHashSet();

		for (Class<?> adaptableClass : AdapterManager.peripherals.getAllAdaptableClasses()) {
			if (blacklist.contains(adaptableClass.getName())) continue;
			if (TileEntity.class.isAssignableFrom(adaptableClass)) {
				// no need to continue, since CC does .isAssignableFrom when
				// searching for peripheral
				interestingClasses.add((Class<? extends TileEntity>)adaptableClass);
			} else if (!adaptableClass.isInterface()) {
				Log.warn("Class %s is neither interface nor TileEntity. Skipping peripheral registration.", adaptableClass);
			} else {
				Iterator<Class<? extends TileEntity>> it = candidates.iterator();
				while (it.hasNext()) {
					Class<? extends TileEntity> teClass = it.next();
					if (adaptableClass.isAssignableFrom(teClass)) {
						interestingClasses.add(teClass);
						it.remove();
					}
				}
			}
		}

		final int providerCount = providerClasses.size();
		final int adapterCount = adaptedClasses.size();
		Log.info("Registering peripheral handler for %d classes (providers: %d, adapters: %d))", providerCount + adapterCount, providerCount, adapterCount);

		adaptedClasses.clear();
		for (Class<? extends TileEntity> teClass : interestingClasses) {
			if (teClass.isAnnotationPresent(Volatile.class)) {
				Log.fine("Adding non-caching adapter handler for %s", teClass);
				adaptedClasses.put(teClass, ADAPTER_HANDLER);
			} else {
				Log.fine("Adding caching adapter handler for %s", teClass);
				adaptedClasses.put(teClass, ADAPTER_CACHING_HANDLER);
			}
		}

		for (Class<? extends TileEntity> teClass : providerClasses) {
			if (teClass.isAnnotationPresent(Volatile.class)) {
				Log.fine("Adding non-caching provider handler for %s", teClass);
				adaptedClasses.put(teClass, PROVIDER_HANDLER);
			} else {
				Log.fine("Adding caching provider handler for %s", teClass);
				adaptedClasses.put(teClass, PROVIDER_CACHING_HANDLER);
			}
		}
	}

	public static IPeripheral createPeripheral(Object target) {
		ClassMethodsList<IPeripheralMethodExecutor> adapter = AdapterManager.peripherals.adaptClass(target.getClass());
		return new AdapterPeripheral(adapter, target);
	}

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te == null) return null;

		IPeripheralFactory<TileEntity> factory = adaptedClasses.get(te.getClass());
		if (factory == null) return null;

		return factory.getPeripheral(te, side);
	}
}
