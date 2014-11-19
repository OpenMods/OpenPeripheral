package openperipheral.adapter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmods.Log;
import openmods.reflection.ReflectionHelper;
import openperipheral.adapter.peripheral.AdapterPeripheral;
import openperipheral.adapter.peripheral.IPeripheralMethodExecutor;
import openperipheral.adapter.peripheral.ProxyAdapterPeripheral;
import openperipheral.api.ExposeInterface;
import openperipheral.api.ICustomPeripheralProvider;
import openperipheral.api.Volatile;

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

		if (TileEntityBlacklist.INSTANCE.isIgnored(teClass)) return NULL_HANDLER;

		for (Class<?> adaptableClass : AdapterManager.PERIPHERALS_MANAGER.getAllAdaptableClasses()) {
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
		MethodMap<IPeripheralMethodExecutor> methods = AdapterManager.PERIPHERALS_MANAGER.getAdaptedClass(targetClass);
		if (methods.isEmpty()) return null;

		ExposeInterface proxyAnn = targetClass.getAnnotation(ExposeInterface.class);
		if (proxyAnn == null) return new AdapterPeripheral(methods, target);

		Set<Class<?>> implemented = ReflectionHelper.getAllInterfaces(targetClass);
		Set<Class<?>> whitelist = ImmutableSet.copyOf(proxyAnn.value());
		Set<Class<?>> proxied = Sets.intersection(implemented, whitelist);

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
