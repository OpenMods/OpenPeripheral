package openperipheral.interfaces.cc.providers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmods.Log;
import openmods.reflection.ReflectionHelper;
import openperipheral.adapter.TileEntityBlacklist;
import openperipheral.adapter.composed.IndexedMethodMap;
import openperipheral.api.adapter.GenerationFailedException;
import openperipheral.api.architecture.cc.ICustomPeripheralProvider;
import openperipheral.api.peripheral.ExposeInterface;
import openperipheral.api.peripheral.IOpenPeripheral;
import openperipheral.api.peripheral.Volatile;
import openperipheral.interfaces.cc.ModuleComputerCraft;
import openperipheral.interfaces.cc.wrappers.AdapterPeripheral;
import openperipheral.interfaces.cc.wrappers.ProxyAdapterPeripheral;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

public class PeripheralProvider implements IPeripheralProvider {
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

		if (TileEntityBlacklist.INSTANCE.isBlacklisted(teClass)) return NULL_HANDLER;

		if (teClass.isAnnotationPresent(Volatile.class)) {
			Log.trace("Adding non-caching adapter handler for %s", teClass);
			return ADAPTER_HANDLER;
		} else {
			Log.trace("Adding caching adapter handler for %s", teClass);
			return ADAPTER_CACHING_HANDLER;
		}
	}

	private static IPeripheralFactory<TileEntity> getFactoryForClass(Class<? extends TileEntity> teClass) {
		IPeripheralFactory<TileEntity> factory = adaptedClasses.get(teClass);

		if (factory == null) {
			factory = findFactoryForClass(teClass);
			adaptedClasses.put(teClass, factory);
		}

		return factory;
	}

	public static IPeripheral createAdaptedPeripheralWrapped(Object target) {
		Preconditions.checkNotNull(target, "Null target");
		try {
			return createAdaptedPeripheral(target);
		} catch (Throwable t) {
			throw new GenerationFailedException(String.format("%s (%s)", target, target.getClass()), t);
		}
	}

	public static IPeripheral createAdaptedPeripheral(Object target) {
		Class<?> targetClass = target.getClass();
		IndexedMethodMap methods = ModuleComputerCraft.PERIPHERAL_METHODS_FACTORY.getAdaptedClass(targetClass);
		if (methods.isEmpty()) return null;

		ExposeInterface proxyAnn = targetClass.getAnnotation(ExposeInterface.class);
		if (proxyAnn == null) return new AdapterPeripheral(methods, target);

		Set<Class<?>> implemented = ReflectionHelper.getAllInterfaces(targetClass);
		Set<Class<?>> whitelist = ImmutableSet.copyOf(proxyAnn.value());
		Set<Class<?>> proxied = Sets.intersection(implemented, whitelist);

		if (proxied.isEmpty()) return new AdapterPeripheral(methods, target);

		Set<Class<?>> allImplemented = Sets.newHashSet(proxied);
		allImplemented.add(IPeripheral.class);
		allImplemented.add(IOpenPeripheral.class);

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
