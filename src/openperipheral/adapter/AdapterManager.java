package openperipheral.adapter;

import java.util.*;

import openmods.Log;
import openperipheral.adapter.composed.ClassMethodsComposer;
import openperipheral.adapter.composed.ClassMethodsList;
import openperipheral.adapter.composed.ClassMethodsListBuilder;
import openperipheral.adapter.object.*;
import openperipheral.adapter.peripheral.*;
import openperipheral.api.IAdapterBase;
import openperipheral.api.IObjectAdapter;
import openperipheral.api.IPeripheralAdapter;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;

import dan200.computercraft.api.lua.ILuaObject;

public abstract class AdapterManager<A extends IAdapterBase, E extends IMethodExecutor> {

	private static final ClassMethodsComposer<IObjectMethodExecutor> OBJECT_COMPOSER = new ClassMethodsComposer<IObjectMethodExecutor>() {
		@Override
		protected ClassMethodsListBuilder<IObjectMethodExecutor> createBuilder() {
			return new ObjectMethodsListBuilder();
		}
	};

	public static final AdapterManager<IObjectAdapter, IObjectMethodExecutor> objects = new AdapterManager<IObjectAdapter, IObjectMethodExecutor>() {

		@Override
		protected ClassMethodsList<IObjectMethodExecutor> adaptClass(Class<?> targetClass) {
			return OBJECT_COMPOSER.createMethodsList(targetClass);
		}

		@Override
		protected IMethodsList<IObjectMethodExecutor> wrapExternalAdapter(IObjectAdapter adapter) {
			return new ObjectAdapterWrapper.External(adapter);
		}

		@Override
		protected IMethodsList<IObjectMethodExecutor> wrapInlineAdapter(Class<?> targetClass) {
			return new ObjectAdapterWrapper.Inline(targetClass);
		}
	};

	private static final ClassMethodsComposer<IPeripheralMethodExecutor> PERIPHERAL_COMPOSER = new ClassMethodsComposer<IPeripheralMethodExecutor>() {
		@Override
		protected ClassMethodsListBuilder<IPeripheralMethodExecutor> createBuilder() {
			return new PeripheralMethodsListBuilder();
		}
	};

	public static final AdapterManager<IPeripheralAdapter, IPeripheralMethodExecutor> peripherals = new AdapterManager<IPeripheralAdapter, IPeripheralMethodExecutor>() {
		@Override
		protected ClassMethodsList<IPeripheralMethodExecutor> adaptClass(Class<?> targetClass) {
			return PERIPHERAL_COMPOSER.createMethodsList(targetClass);
		}

		@Override
		protected IMethodsList<IPeripheralMethodExecutor> wrapExternalAdapter(IPeripheralAdapter adapter) {
			return new PeripheralExternalAdapterWrapper(adapter);
		}

		@Override
		protected IMethodsList<IPeripheralMethodExecutor> wrapInlineAdapter(Class<?> targetClass) {
			return new PeripheralInlineAdapterWrapper(targetClass);
		}
	};

	private final Multimap<Class<?>, IMethodsList<E>> externalAdapters = HashMultimap.create();

	private final Map<Class<?>, IMethodsList<E>> internalAdapters = Maps.newHashMap();

	private final Map<Class<?>, ClassMethodsList<E>> classes = Maps.newHashMap();

	public static void addObjectAdapter(IObjectAdapter adapter) {
		objects.addAdapter(adapter);
	}

	public static void addPeripheralAdapter(IPeripheralAdapter adapter) {
		peripherals.addAdapter(adapter);
	}

	public static void addInlinePeripheralAdapter(Class<?> cls) {
		peripherals.addInlineAdapter(cls);
	}

	public Set<Class<?>> getAllAdaptableClasses() {
		return Sets.union(externalAdapters.keySet(), internalAdapters.keySet());
	}

	public void addAdapter(A adapter) {
		final IMethodsList<E> wrapper;
		try {
			wrapper = wrapExternalAdapter(adapter);
		} catch (Throwable e) {
			Log.warn(e, "Something went terribly wrong while adding internal adapter '%s'. It will be disabled", adapter.getClass());
			return;
		}
		final Class<?> targetCls = wrapper.getTargetClass();
		Preconditions.checkArgument(!Object.class.equals(wrapper.getTargetClass()), "Can't add adapter for Object class");

		Log.info("Registering %s adapter for class %s", wrapper.describeType(), targetCls);
		externalAdapters.put(wrapper.getTargetClass(), wrapper);
	}

	public void addInlineAdapter(Class<?> targetCls) {
		IMethodsList<E> wrapper = wrapInlineAdapter(targetCls);
		Log.info("Registering auto-created adapter for class %s", targetCls);
		internalAdapters.put(targetCls, wrapper);
	}

	public ClassMethodsList<E> getAdapterClass(Class<?> targetCls) {
		ClassMethodsList<E> value = classes.get(targetCls);
		if (value == null) {
			value = adaptClass(targetCls);
			classes.put(targetCls, value);
		}

		return value;
	}

	public Collection<IMethodsList<E>> getExternalAdapters(Class<?> targetCls) {
		return Collections.unmodifiableCollection(externalAdapters.get(targetCls));
	}

	public IMethodsList<E> getInlineAdapter(Class<?> targetCls) {
		IMethodsList<E> wrapper = internalAdapters.get(targetCls);
		if (wrapper == null) {
			wrapper = wrapInlineAdapter(targetCls);
			internalAdapters.put(targetCls, wrapper);
		}

		return wrapper;
	}

	protected abstract ClassMethodsList<E> adaptClass(Class<?> targetClass);

	protected abstract IMethodsList<E> wrapExternalAdapter(A adapter);

	protected abstract IMethodsList<E> wrapInlineAdapter(Class<?> targetClass);

	public static ILuaObject wrapObject(Object o) {
		return LuaObjectWrapper.wrap(objects, o);
	}
}
