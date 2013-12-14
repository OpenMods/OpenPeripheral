package openperipheral.adapter;

import java.util.*;

import net.minecraft.tileentity.TileEntity;
import openmods.Log;
import openperipheral.adapter.object.IObjectMethodExecutor;
import openperipheral.adapter.object.LuaObjectWrapper;
import openperipheral.adapter.object.ObjectAdaptedClass;
import openperipheral.adapter.object.ObjectAdapterWrapper;
import openperipheral.adapter.peripheral.*;
import openperipheral.api.IAdapterBase;
import openperipheral.api.IObjectAdapter;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.IPeripheralProvider;
import openperipheral.util.PeripheralUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import dan200.computer.api.ComputerCraftAPI;
import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.ILuaObject;
import dan200.computer.api.IPeripheralHandler;

public abstract class AdapterManager<A extends IAdapterBase, E extends IMethodExecutor> {

	private static final IPeripheralHandler peripheralHandler = new IPeripheralHandler() {

		private Map<TileEntity, IHostedPeripheral> created = new WeakHashMap<TileEntity, IHostedPeripheral>();

		@Override
		public IHostedPeripheral getPeripheral(TileEntity tile) {
			if (tile == null) return null;

			IHostedPeripheral peripheral = created.get(tile);

			if (peripheral == null) {

				if (tile instanceof IPeripheralProvider) {
					peripheral = ((IPeripheralProvider)tile).providePeripheral(tile.worldObj);
				} else {
					AdaptedClass<IPeripheralMethodExecutor> adapter = peripherals.adaptClass(tile.getClass());
					peripheral = new HostedPeripheral(adapter, tile);
				}

				created.put(tile, peripheral);
			}

			return peripheral;
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

	public static void registerPeripherals() {
		Map<Class<? extends TileEntity>, String> classToNameMap = PeripheralUtils.getClassToNameMap();
		Set<Class<?>> classesWithAdapters = peripherals.getAllClasses();

		for (Map.Entry<Class<? extends TileEntity>, String> e : classToNameMap.entrySet()) {
			Class<? extends TileEntity> teClass = e.getKey();
			if (teClass == null) {
				Log.warn("TE with id %s has null key", e.getValue());
				continue;
			}

			if (IPeripheralProvider.class.isAssignableFrom(teClass)) {
				ComputerCraftAPI.registerExternalPeripheral(teClass, peripheralHandler);
				continue;
			}

			for (Class<?> adaptableClass : classesWithAdapters) {
				if (adaptableClass.isAssignableFrom(teClass)) {
					ComputerCraftAPI.registerExternalPeripheral(teClass, peripheralHandler);
					break;
				}
			}
		}
	}

	private final Multimap<Class<?>, AdapterWrapper<E>> externalAdapters = HashMultimap.create();

	private final Map<Class<?>, AdapterWrapper<E>> internalAdapters = Maps.newHashMap();

	private final Map<Class<?>, AdaptedClass<E>> classes = Maps.newHashMap();

	private Set<Class<?>> getAllClasses() {
		return Sets.union(externalAdapters.keySet(), internalAdapters.keySet());
	}

	public void addAdapter(A adapter) {
		final AdapterWrapper<E> wrapper = wrapExternalAdapter(adapter);
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

	public static HostedPeripheral createHostedPeripheral(Object target) {
		AdaptedClass<IPeripheralMethodExecutor> adapter = peripherals.adaptClass(target.getClass());
		return new HostedPeripheral(adapter, target);
	}
}
