package openperipheral.adapter;

import java.util.*;

import net.minecraft.tileentity.TileEntity;
import openmods.Log;
import openperipheral.Config;
import openperipheral.adapter.object.*;
import openperipheral.adapter.peripheral.*;
import openperipheral.api.*;
import openperipheral.util.PeripheralUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;

import dan200.computer.api.*;

public abstract class AdapterManager<A extends IAdapterBase, E extends IMethodExecutor> {

	private static final IPeripheralHandler ADAPTER_HANDLER = new CachingPeripheralHandler() {
		@Override
		protected IHostedPeripheral createPeripheral(TileEntity tile) {
			AdaptedClass<IPeripheralMethodExecutor> adapter = peripherals.adaptClass(tile.getClass());
			return new HostedPeripheral(adapter, tile);
		}
	};

	private static final IPeripheralHandler ADAPTER_CACHING_HANDLER = new CachingPeripheralHandler() {
		@Override
		protected IHostedPeripheral createPeripheral(TileEntity tile) {
			AdaptedClass<IPeripheralMethodExecutor> adapter = peripherals.adaptClass(tile.getClass());
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

	public static final AdapterManager<IObjectAdapter, IObjectMethodExecutor> objects = new AdapterManager<IObjectAdapter, IObjectMethodExecutor>() {

		@Override
		protected AdaptedClass<IObjectMethodExecutor> adaptClass(Class<?> targetClass) {
			return new ObjectAdaptedClass(this, targetClass);
		}

		@Override
		protected AdapterWrapper<IObjectMethodExecutor> wrapExternalAdapter(IObjectAdapter adapter) {
			return new ObjectAdapterWrapper.External(adapter);
		}

		@Override
		protected AdapterWrapper<IObjectMethodExecutor> wrapInlineAdapter(Class<?> targetClass) {
			return new ObjectAdapterWrapper.Inline(targetClass);
		}
	};

	public static final AdapterManager<IPeripheralAdapter, IPeripheralMethodExecutor> peripherals = new AdapterManager<IPeripheralAdapter, IPeripheralMethodExecutor>() {
		@Override
		protected AdaptedClass<IPeripheralMethodExecutor> adaptClass(Class<?> targetClass) {
			return new PeripheralAdaptedClass(this, targetClass);
		}

		@Override
		protected AdapterWrapper<IPeripheralMethodExecutor> wrapExternalAdapter(IPeripheralAdapter adapter) {
			return new PeripheralExternalAdapterWrapper(adapter);
		}

		@Override
		protected AdapterWrapper<IPeripheralMethodExecutor> wrapInlineAdapter(Class<?> targetClass) {
			return new PeripheralInlineAdapterWrapper(targetClass);
		}
	};

	public static void addObjectAdapter(IObjectAdapter adapter) {
		objects.addAdapter(adapter);
	}

	public static void addPeripheralAdapter(IPeripheralAdapter adapter) {
		peripherals.addAdapter(adapter);
	}

	public static void addInlinePeripheralAdapter(Class<?> cls) {
		peripherals.addInlineAdapter(cls);
	}

	private Set<Class<?>> getAllClasses() {
		return Sets.union(externalAdapters.keySet(), internalAdapters.keySet());
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

		for (Class<?> adaptableClass : peripherals.getAllClasses()) {
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

	public static Collection<Class<? extends TileEntity>> getAllAdaptedClasses() {
		return Collections.unmodifiableCollection(adaptedClasses);
	}

	private final Multimap<Class<?>, AdapterWrapper<E>> externalAdapters = HashMultimap.create();

	private final Map<Class<?>, AdapterWrapper<E>> internalAdapters = Maps.newHashMap();

	private final Map<Class<?>, AdaptedClass<E>> classes = Maps.newHashMap();

	private static final Set<Class<? extends TileEntity>> adaptedClasses = Sets.newHashSet();

	public void addAdapter(A adapter) {
		final AdapterWrapper<E> wrapper;
		try {
			wrapper = wrapExternalAdapter(adapter);
		} catch (Throwable e) {
			Log.warn(e, "Something went terribly wrong while adding internal adapter '%s'. It will be disabled", adapter.getClass());
			return;
		}
		final Class<?> targetCls = wrapper.targetCls;
		Preconditions.checkArgument(!Object.class.equals(wrapper.targetCls), "Can't add adapter for Object class");

		Log.info("Registering adapter %s for class %s", wrapper.adapterClass, targetCls);
		externalAdapters.put(wrapper.targetCls, wrapper);
	}

	public void addInlineAdapter(Class<?> targetCls) {
		AdapterWrapper<E> wrapper = wrapInlineAdapter(targetCls);

		Log.info("Registering auto-created adapter for class %s", targetCls);
		internalAdapters.put(targetCls, wrapper);
	}

	public AdaptedClass<E> getAdapterClass(Class<?> targetCls) {
		AdaptedClass<E> value = classes.get(targetCls);
		if (value == null) {
			value = adaptClass(targetCls);
			classes.put(targetCls, value);
		}

		return value;
	}

	Collection<AdapterWrapper<E>> getExternalAdapters(Class<?> targetCls) {
		return Collections.unmodifiableCollection(externalAdapters.get(targetCls));
	}

	AdapterWrapper<E> getInlineAdapter(Class<?> targetCls) {
		AdapterWrapper<E> wrapper = internalAdapters.get(targetCls);
		if (wrapper == null) {
			wrapper = wrapInlineAdapter(targetCls);
			internalAdapters.put(targetCls, wrapper);
		}

		return wrapper;
	}

	protected abstract AdaptedClass<E> adaptClass(Class<?> targetClass);

	protected abstract AdapterWrapper<E> wrapExternalAdapter(A adapter);

	protected abstract AdapterWrapper<E> wrapInlineAdapter(Class<?> targetClass);

	public static ILuaObject wrapObject(Object o) {
		return LuaObjectWrapper.wrap(objects, o);
	}

	public static IHostedPeripheral createHostedPeripheral(Object target) {
		AdaptedClass<IPeripheralMethodExecutor> adapter = peripherals.adaptClass(target.getClass());
		if (target instanceof IUpdateHandler) return new TickingHostedPeripheral(adapter, (IUpdateHandler)target);
		return new HostedPeripheral(adapter, target);
	}
}
