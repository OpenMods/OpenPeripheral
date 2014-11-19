package openperipheral.adapter;

import java.util.*;

import openmods.Log;
import openperipheral.adapter.composed.ClassMethodsComposer;
import openperipheral.adapter.composed.ClassMethodsListBuilder;
import openperipheral.adapter.object.*;
import openperipheral.adapter.peripheral.*;
import openperipheral.api.IAdapter;
import openperipheral.api.IObjectAdapter;
import openperipheral.api.IPeripheralAdapter;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;

import dan200.computercraft.api.lua.ILuaObject;

public abstract class AdapterManager<E extends IMethodExecutor> {

	public static class InvalidClassException extends RuntimeException {
		private static final long serialVersionUID = 5722017683388067641L;

		private InvalidClassException() {
			super();
		}

		private InvalidClassException(Throwable cause) {
			super(cause);
		}
	}

	private static final ClassMethodsComposer<IObjectMethodExecutor> OBJECT_COMPOSER = new ClassMethodsComposer<IObjectMethodExecutor>() {
		@Override
		protected ClassMethodsListBuilder<IObjectMethodExecutor> createBuilder() {
			return new ObjectMethodsListBuilder();
		}
	};

	public static final AdapterManager<IObjectMethodExecutor> OBJECTS_MANAGER = new AdapterManager<IObjectMethodExecutor>() {

		@Override
		protected MethodMap<IObjectMethodExecutor> collectMethods(Class<?> targetClass) {
			return OBJECT_COMPOSER.createMethodsList(targetClass);
		}

		@Override
		protected AdapterWrapper<IObjectMethodExecutor> wrapExternalAdapter(IAdapter adapter) {
			return new ObjectAdapterWrapper.External(adapter);
		}

		@Override
		protected AdapterWrapper<IObjectMethodExecutor> wrapInlineAdapter(Class<?> targetClass) {
			return new ObjectAdapterWrapper.Inline(targetClass);
		}
	};

	private static final ClassMethodsComposer<IPeripheralMethodExecutor> PERIPHERAL_COMPOSER = new ClassMethodsComposer<IPeripheralMethodExecutor>() {
		@Override
		protected ClassMethodsListBuilder<IPeripheralMethodExecutor> createBuilder() {
			return new PeripheralMethodsListBuilder();
		}
	};

	public static final AdapterManager<IPeripheralMethodExecutor> PERIPHERALS_MANAGER = new AdapterManager<IPeripheralMethodExecutor>() {
		@Override
		protected MethodMap<IPeripheralMethodExecutor> collectMethods(Class<?> targetClass) {
			return PERIPHERAL_COMPOSER.createMethodsList(targetClass);
		}

		@Override
		protected AdapterWrapper<IPeripheralMethodExecutor> wrapExternalAdapter(IAdapter adapter) {
			return new PeripheralExternalAdapterWrapper(adapter);
		}

		@Override
		protected AdapterWrapper<IPeripheralMethodExecutor> wrapInlineAdapter(Class<?> targetClass) {
			return new PeripheralInlineAdapterWrapper(targetClass);
		}
	};

	private final Multimap<Class<?>, AdapterWrapper<E>> externalAdapters = HashMultimap.create();

	private final Map<Class<?>, AdapterWrapper<E>> internalAdapters = Maps.newHashMap();

	private final Map<Class<?>, MethodMap<E>> classes = Maps.newHashMap();

	private final Set<Class<?>> invalidClasses = Sets.newHashSet();

	public static boolean addObjectAdapter(IObjectAdapter adapter) {
		return OBJECTS_MANAGER.addAdapter(adapter);
	}

	public static boolean addPeripheralAdapter(IPeripheralAdapter adapter) {
		return PERIPHERALS_MANAGER.addAdapter(adapter);
	}

	public static void addInlinePeripheralAdapter(Class<?> cls) {
		PERIPHERALS_MANAGER.addInlineAdapter(cls);
	}

	public Set<Class<?>> getAllAdaptableClasses() {
		return Sets.union(externalAdapters.keySet(), internalAdapters.keySet());
	}

	public Map<Class<?>, Collection<AdapterWrapper<E>>> listExternalAdapters() {
		return Collections.unmodifiableMap(externalAdapters.asMap());
	}

	public Map<Class<?>, AdapterWrapper<E>> listInternalAdapters() {
		return Collections.unmodifiableMap(internalAdapters);
	}

	public Map<Class<?>, MethodMap<E>> listCollectedClasses() {
		return Collections.unmodifiableMap(classes);
	}

	public boolean addAdapter(IAdapter adapter) {
		final AdapterWrapper<E> wrapper;
		try {
			wrapper = wrapExternalAdapter(adapter);
		} catch (Throwable e) {
			Log.warn(e, "Something went terribly wrong while adding internal adapter '%s'. It will be disabled", adapter.getClass());
			return false;
		}
		final Class<?> targetCls = adapter.getTargetClass();
		Preconditions.checkArgument(!Object.class.equals(targetCls), "Can't add adapter for Object class");

		Log.trace("Registering %s adapter (source id: %s) for %s", wrapper.describe(), wrapper.source(), targetCls);
		externalAdapters.put(targetCls, wrapper);
		return true;
	}

	public void addInlineAdapter(Class<?> targetCls) {
		AdapterWrapper<E> wrapper = wrapInlineAdapter(targetCls);
		Log.trace("Registering %s adapter (source id: %s) adapter for %s", wrapper.describe(), wrapper.source(), targetCls);
		internalAdapters.put(targetCls, wrapper);
	}

	public MethodMap<E> getAdaptedClass(Class<?> targetCls) {
		if (invalidClasses.contains(targetCls)) throw new InvalidClassException();

		MethodMap<E> value = classes.get(targetCls);
		if (value == null) {
			try {
				value = collectMethods(targetCls);
			} catch (Throwable t) {
				invalidClasses.add(targetCls);
				throw new InvalidClassException(t);
			}

			classes.put(targetCls, value);
		}

		return value;
	}

	public Collection<AdapterWrapper<E>> getExternalAdapters(Class<?> targetCls) {
		return Collections.unmodifiableCollection(externalAdapters.get(targetCls));
	}

	public AdapterWrapper<E> getInlineAdapter(Class<?> targetCls) {
		AdapterWrapper<E> wrapper = internalAdapters.get(targetCls);
		if (wrapper == null) {
			wrapper = wrapInlineAdapter(targetCls);
			internalAdapters.put(targetCls, wrapper);
		}

		return wrapper;
	}

	protected abstract MethodMap<E> collectMethods(Class<?> targetClass);

	protected abstract AdapterWrapper<E> wrapExternalAdapter(IAdapter adapter);

	protected abstract AdapterWrapper<E> wrapInlineAdapter(Class<?> targetClass);

	public static ILuaObject wrapObject(Object o) {
		return LuaObjectWrapper.wrap(OBJECTS_MANAGER, o);
	}
}
