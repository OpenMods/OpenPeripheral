package openperipheral.adapter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmods.Log;
import openmods.utils.ReflectionHelper;
import openperipheral.Config;
import openperipheral.adapter.composed.ClassMethodsList;
import openperipheral.adapter.peripheral.AdapterPeripheral;
import openperipheral.adapter.peripheral.IPeripheralMethodExecutor;
import openperipheral.adapter.peripheral.ProxyAdapterPeripheral;
import openperipheral.api.*;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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
			return createAdaptedPeripheral(tile);
		}

	};

	private static final IPeripheralFactory<TileEntity> ADAPTER_CACHING_HANDLER = new CachingPeripheralFactory() {
		@Override
		protected IPeripheral createPeripheral(TileEntity tile, int side) {
			return createAdaptedPeripheral(tile);
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

	private static IPeripheralFactory<TileEntity> findFactoryForClass(Class<? extends TileEntity> teClass) {
		if (IPeripheral.class.isAssignableFrom(teClass)) return NULL_HANDLER;

		if (ICustomPeripheralProvider.class.isAssignableFrom(teClass)) {
			if (teClass.isAnnotationPresent(Volatile.class)) {
				Log.trace("Adding non-caching provider handler for %s", teClass);
				return PROVIDER_HANDLER;
			} else {
				Log.trace("Adding caching provider handler for %s", teClass);
				return PROVIDER_CACHING_HANDLER;
			}
		}

		if (isIgnored(teClass)) return NULL_HANDLER;

		for (Class<?> adaptableClass : AdapterManager.peripherals.getAllAdaptableClasses()) {
			if (adaptableClass.isAssignableFrom(teClass)) {
				if (teClass.isAnnotationPresent(Volatile.class)) {
					Log.trace("Adding non-caching adapter handler for %s", teClass);
					return ADAPTER_HANDLER;
				} else {
					Log.trace("Adding caching adapter handler for %s", teClass);
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

	public static IPeripheral createAdaptedPeripheralSafe(Object target) {
		try {
			return createAdaptedPeripheral(target);
		} catch (Throwable t) {
			Log.warn(t, "Failed to create peripheral for turtle");
			return SafePeripheralFactory.PLACEHOLDER;
		}
	}

	public static IPeripheral createAdaptedPeripheral(Object target) {
		Class<?> targetClass = target.getClass();
		ClassMethodsList<IPeripheralMethodExecutor> methods = AdapterManager.peripherals.getAdapterClass(targetClass);

		ProxyInterfaces proxyAnn = targetClass.getAnnotation(ProxyInterfaces.class);
		if (proxyAnn == null) return new AdapterPeripheral(methods, target);

		Set<Class<?>> implemented = ReflectionHelper.getAllInterfaces(targetClass);
		Set<Class<?>> blacklist = ImmutableSet.copyOf(proxyAnn.exclude());
		Set<Class<?>> proxied = Sets.difference(implemented, blacklist);

		if (proxied.isEmpty()) return new AdapterPeripheral(methods, target);

		Set<Class<?>> allImplemented = Sets.newHashSet(proxied);
		allImplemented.add(IPeripheral.class);

		InvocationHandler handler = new ProxyAdapterPeripheral(methods, target);

		Class<?>[] interfaces = allImplemented.toArray(new Class<?>[allImplemented.size()]);

		return (IPeripheral)Proxy.newProxyInstance(targetClass.getClassLoader(), interfaces, handler);
	}

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te == null) return null;

		IPeripheralFactory<TileEntity> factory = getFactoryForClass(te.getClass());

		return factory.getPeripheral(te, side);
	}
}
