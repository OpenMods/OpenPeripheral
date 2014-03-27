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
import openperipheral.api.Ignore;
import openperipheral.api.Volatile;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

public class PeripheralHandlers implements IPeripheralProvider {
	private static final IPeripheralFactory<TileEntity> NULL_HANDLER = new IPeripheralFactory<TileEntity>() {

		@Override
		public IPeripheral getPeripheral(TileEntity obj, int side) {
			return null;
		}
	};

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

	private static final Set<String> blacklist = ImmutableSet.copyOf(Config.teBlacklist);

	public static Collection<Class<? extends TileEntity>> getAllAdaptedTeClasses() {
		return Collections.unmodifiableCollection(adaptedClasses.keySet());
	}

	private static IPeripheralFactory<TileEntity> findFactoryForClass(Class<? extends TileEntity> teClass) {
		if (IPeripheral.class.isAssignableFrom(teClass)) return NULL_HANDLER;

		if (ICustomPeripheralProvider.class.isAssignableFrom(teClass)) {
			if (teClass.isAnnotationPresent(Volatile.class)) {
				Log.fine("Adding non-caching provider handler for %s", teClass);
				return PROVIDER_HANDLER;
			} else {
				Log.fine("Adding caching provider handler for %s", teClass);
				return PROVIDER_CACHING_HANDLER;
			}
		}

		if (isIgnored(teClass)) return NULL_HANDLER;

		for (Class<?> adaptableClass : AdapterManager.peripherals.getAllAdaptableClasses()) {
			if (adaptableClass.isAssignableFrom(teClass)) {
				if (teClass.isAnnotationPresent(Volatile.class)) {
					Log.fine("Adding non-caching adapter handler for %s", teClass);
					return ADAPTER_HANDLER;
				} else {
					Log.fine("Adding caching adapter handler for %s", teClass);
					return ADAPTER_CACHING_HANDLER;
				}
			}
		}

		return NULL_HANDLER;
	}

	private static IPeripheralFactory<TileEntity> getFactoryForClass(Class<? extends TileEntity> teClass) {
		IPeripheralFactory<TileEntity> factory = adaptedClasses.get(teClass);

		if (factory == null) {
			factory = findFactoryForClass(teClass);
			adaptedClasses.put(teClass, factory);
		}

		return factory;
	}

	protected static boolean isIgnored(Class<? extends TileEntity> teClass) {
		final String teClassName = teClass.getName();
		if (blacklist.contains(teClassName) || blacklist.contains(teClassName.toLowerCase())) return true;

		if (teClass.isAnnotationPresent(Ignore.class)) return true;

		try {
			teClass.getField("OPENPERIPHERAL_IGNORE");
			return true;
		} catch (NoSuchFieldException e) {
			// uff, we are not ignored
		} catch (Throwable t) {
			Log.warn(t, "Class %s doesn't cooperate", teClass);
		}

		return false;
	}

	public static IPeripheral createPeripheral(Object target) {
		ClassMethodsList<IPeripheralMethodExecutor> adapter = AdapterManager.peripherals.adaptClass(target.getClass());
		return new AdapterPeripheral(adapter, target);
	}

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te == null) return null;

		IPeripheralFactory<TileEntity> factory = getFactoryForClass(te.getClass());

		return factory.getPeripheral(te, side);
	}
}
