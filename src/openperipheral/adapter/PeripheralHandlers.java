package openperipheral.adapter;

import java.util.*;

import net.minecraft.tileentity.TileEntity;
import openmods.Log;
import openperipheral.Config;
import openperipheral.adapter.peripheral.HostedPeripheral;
import openperipheral.adapter.peripheral.IPeripheralMethodExecutor;
import openperipheral.adapter.peripheral.TickingHostedPeripheral;
import openperipheral.api.IPeripheralProvider;
import openperipheral.api.IUpdateHandler;
import openperipheral.api.Volatile;
import openperipheral.util.PeripheralUtils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import dan200.computercraft.api.*;
import dan200.computercraft.api.peripheral.*;

public class PeripheralHandlers {
	private static final IPeripheralHandler ADAPTER_HANDLER = new CachingPeripheralHandler() {
		@Override
		protected IHostedPeripheral createPeripheral(TileEntity tile) {
			AdaptedClass<IPeripheralMethodExecutor> adapter = AdapterManager.peripherals.adaptClass(tile.getClass());
			return new HostedPeripheral(adapter, tile);
		}
	};

	private static final IPeripheralHandler ADAPTER_CACHING_HANDLER = new CachingPeripheralHandler() {
		@Override
		protected IHostedPeripheral createPeripheral(TileEntity tile) {
			AdaptedClass<IPeripheralMethodExecutor> adapter = AdapterManager.peripherals.adaptClass(tile.getClass());
			return new HostedPeripheral(adapter, tile);
		}
	};

	private static final IPeripheralHandler PROVIDER_HANDLER = new SafePeripheralHandler() {
		@Override
		protected IHostedPeripheral createPeripheral(TileEntity tile) {
			return (tile instanceof IPeripheralProvider)? ((IPeripheralProvider)tile).providePeripheral(tile.worldObj) : null;
		}
	};

	private static final IPeripheralHandler PROVIDER_CACHING_HANDLER = new CachingPeripheralHandler() {
		@Override
		protected IHostedPeripheral createPeripheral(TileEntity tile) {
			return (tile instanceof IPeripheralProvider)? ((IPeripheralProvider)tile).providePeripheral(tile.worldObj) : null;
		}
	};

	private static final Set<Class<? extends TileEntity>> adaptedClasses = Sets.newHashSet();

	public static Collection<Class<? extends TileEntity>> getAllAdaptedTeClasses() {
		return Collections.unmodifiableCollection(adaptedClasses);
	}

	@SuppressWarnings("unchecked")
	public static void registerPeripherals() {
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
			} else if (IPeripheralProvider.class.isAssignableFrom(teClass)) {
				providerClasses.add(teClass);
			} else if (!IPeripheral.class.isAssignableFrom(teClass)) {
				candidates.add(teClass);
			}

		}

		adaptedClasses.clear();

		for (Class<?> adaptableClass : AdapterManager.peripherals.getAllAdaptableClasses()) {
			if (blacklist.contains(adaptableClass.getName())) continue;
			if (TileEntity.class.isAssignableFrom(adaptableClass)) {
				// no need to continue, since CC does .isAssignableFrom when
				// searching for peripheral
				adaptedClasses.add((Class<? extends TileEntity>)adaptableClass);
			} else if (!adaptableClass.isInterface()) {
				Log.warn("Class %s is neither interface nor TileEntity. Skipping peripheral registration.", adaptableClass);
			} else {
				Iterator<Class<? extends TileEntity>> it = candidates.iterator();
				while (it.hasNext()) {
					Class<? extends TileEntity> teClass = it.next();
					if (adaptableClass.isAssignableFrom(teClass)) {
						adaptedClasses.add(teClass);
						it.remove();
					}
				}
			}
		}

		final int providerCount = providerClasses.size();
		final int adapterCount = adaptedClasses.size();
		Log.info("Registering peripheral handler for %d classes (providers: %d, adapters: %d))", providerCount + adapterCount, providerCount, adapterCount);

		for (Class<? extends TileEntity> teClass : adaptedClasses) {
			if (teClass.isAnnotationPresent(Volatile.class)) {
				Log.fine("Adding non-caching adapter handler for %s", teClass);
				ComputerCraftAPI.registerExternalPeripheral(teClass, ADAPTER_HANDLER);
			} else {
				Log.fine("Adding caching adapter handler for %s", teClass);
				ComputerCraftAPI.registerExternalPeripheral(teClass, ADAPTER_CACHING_HANDLER);
			}
		}

		for (Class<? extends TileEntity> teClass : providerClasses) {
			if (teClass.isAnnotationPresent(Volatile.class)) {
				Log.fine("Adding non-caching provider handler for %s", teClass);
				ComputerCraftAPI.registerExternalPeripheral(teClass, PROVIDER_HANDLER);
			} else {
				Log.fine("Adding caching provider handler for %s", teClass);
				ComputerCraftAPI.registerExternalPeripheral(teClass, PROVIDER_CACHING_HANDLER);
			}
		}
	}

	public static IHostedPeripheral createHostedPeripheral(Object target) {
		AdaptedClass<IPeripheralMethodExecutor> adapter = AdapterManager.peripherals.adaptClass(target.getClass());
		if (target instanceof IUpdateHandler) return new TickingHostedPeripheral(adapter, (IUpdateHandler)target);
		return new HostedPeripheral(adapter, target);
	}
}
