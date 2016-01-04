package openperipheral.interfaces.cc.providers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import openmods.Log;
import openmods.reflection.ReflectionHelper;
import openmods.utils.CachedFactory;
import openperipheral.adapter.TileEntityBlacklist;
import openperipheral.adapter.composed.IndexedMethodMap;
import openperipheral.api.adapter.GenerationFailedException;
import openperipheral.api.architecture.cc.ICustomPeripheralProvider;
import openperipheral.api.peripheral.ExposeInterface;
import openperipheral.api.peripheral.IOpenPeripheral;
import openperipheral.interfaces.cc.ModuleComputerCraft;
import openperipheral.interfaces.cc.wrappers.AdapterPeripheral;
import openperipheral.interfaces.cc.wrappers.ProxyAdapterPeripheral;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

public class PeripheralProvider implements IPeripheralProvider {
	private static final IPeripheralFactory<TileEntity> NULL_FACTORY = new IPeripheralFactory<TileEntity>() {
		@Override
		public IPeripheral getPeripheral(TileEntity obj, EnumFacing side) {
			return null;
		}
	};

	private static final IPeripheralFactory<TileEntity> PROVIDER_ADAPTER = new SafePeripheralFactory() {
		@Override
		protected IPeripheral createPeripheral(TileEntity tile, EnumFacing side) {
			return ((ICustomPeripheralProvider)tile).createPeripheral(side);
		}
	};

	private static IPeripheralFactory<TileEntity> createDirectFactory(final IndexedMethodMap methods) {
		return new SafePeripheralFactory() {
			@Override
			protected IPeripheral createPeripheral(TileEntity target, EnumFacing side) {
				return new AdapterPeripheral(methods, target);
			}
		};
	}

	private static IPeripheralFactory<TileEntity> createProxyFactory(final IndexedMethodMap methods, Class<?> cls, Set<Class<?>> proxyClasses) {
		final Set<Class<?>> interfaces = appendCommonInterfaces(proxyClasses);
		final Constructor<? extends IPeripheral> ctor = getProxyConstructor(cls, interfaces);
		ctor.setAccessible(true);

		return new SafePeripheralFactory() {
			@Override
			public IPeripheral createPeripheral(TileEntity tile, EnumFacing side) throws Exception {
				final InvocationHandler handler = new ProxyAdapterPeripheral(methods, tile);
				return ctor.newInstance(handler);
			}
		};
	}

	private static Set<Class<?>> appendCommonInterfaces(Set<Class<?>> proxyClasses) {
		final Set<Class<?>> interfaces = Sets.newHashSet(proxyClasses);
		interfaces.add(IPeripheral.class);
		interfaces.add(IOpenPeripheral.class);
		return interfaces;
	}

	private static Constructor<? extends IPeripheral> getProxyConstructor(Class<?> cls, final Set<Class<?>> interfaces) {
		final Class<?>[] tmp = interfaces.toArray(new Class<?>[interfaces.size()]);
		try {
			@SuppressWarnings("unchecked")
			Class<? extends IPeripheral> proxyCls = (Class<? extends IPeripheral>)Proxy.getProxyClass(cls.getClassLoader(), tmp);
			return proxyCls.getConstructor(InvocationHandler.class);
		} catch (Throwable t) {
			throw new RuntimeException(String.format("Failed to create proxy class for %s", cls), t);
		}
	}

	private static IndexedMethodMap getMethodsForClass(Class<?> cls) {
		return ModuleComputerCraft.PERIPHERAL_METHODS_FACTORY.getAdaptedClass(cls);
	}

	private static final CachedFactory<Class<? extends TileEntity>, IPeripheralFactory<TileEntity>> ADAPTED_CLASSES = new CachedFactory<Class<? extends TileEntity>, IPeripheralFactory<TileEntity>>() {
		@Override
		protected IPeripheralFactory<TileEntity> create(Class<? extends TileEntity> targetCls) {
			try {
				if (IPeripheral.class.isAssignableFrom(targetCls)) return NULL_FACTORY;
				if (ICustomPeripheralProvider.class.isAssignableFrom(targetCls)) return PROVIDER_ADAPTER;
				if (TileEntityBlacklist.INSTANCE.isBlacklisted(targetCls)) return NULL_FACTORY;

				final IndexedMethodMap methods = getMethodsForClass(targetCls);
				if (methods.isEmpty()) return NULL_FACTORY;

				final Set<Class<?>> proxyClasses = getProxyClasses(targetCls);
				return proxyClasses.isEmpty()? createDirectFactory(methods) : createProxyFactory(methods, targetCls, proxyClasses);
			} catch (Exception e) {
				Log.warn(e, "Failed to create factory for %s", targetCls);
				return SafePeripheralFactory.BROKEN_FACTORY;
			}
		}
	};

	private static IPeripheralFactory<TileEntity> getFactoryForClass(Class<? extends TileEntity> teClass) {
		return ADAPTED_CLASSES.getOrCreate(teClass);
	}

	public static IPeripheral createAdaptedPeripheralWrapped(Object target) {
		Preconditions.checkNotNull(target, "Null target");
		try {
			return createAdaptedPeripheral(target);
		} catch (Throwable t) {
			throw new GenerationFailedException(String.format("%s (%s)", target, target.getClass()), t);
		}
	}

	private static Set<Class<?>> getProxyClasses(Class<?> target) {
		ExposeInterface proxyAnn = target.getAnnotation(ExposeInterface.class);
		if (proxyAnn == null) return ImmutableSet.of();

		Set<Class<?>> implemented = ReflectionHelper.getAllInterfaces(target);
		Set<Class<?>> whitelist = ImmutableSet.copyOf(proxyAnn.value());
		Set<Class<?>> proxied = Sets.intersection(implemented, whitelist);

		return ImmutableSet.copyOf(proxied);
	}

	public static IPeripheral createAdaptedPeripheral(Object target) {
		final Class<?> targetClass = target.getClass();
		final IndexedMethodMap methods = getMethodsForClass(targetClass);
		if (methods.isEmpty()) return null;

		final Set<Class<?>> proxied = getProxyClasses(targetClass);
		if (proxied.isEmpty()) return new AdapterPeripheral(methods, target);

		final Set<Class<?>> allImplemented = appendCommonInterfaces(proxied);
		final InvocationHandler handler = new ProxyAdapterPeripheral(methods, target);
		final Class<?>[] interfaces = allImplemented.toArray(new Class<?>[allImplemented.size()]);
		return (IPeripheral)Proxy.newProxyInstance(targetClass.getClassLoader(), interfaces, handler);
	}

	@Override
	public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
		final TileEntity te = world.getTileEntity(pos);
		if (te == null) return null;

		final IPeripheralFactory<TileEntity> factory = getFactoryForClass(te.getClass());
		final IPeripheral peripheral = factory.getPeripheral(te, side);
		return peripheral;
	}
}
