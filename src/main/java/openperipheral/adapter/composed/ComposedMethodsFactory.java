package openperipheral.adapter.composed;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import openperipheral.adapter.AdapterRegistry;
import openperipheral.adapter.IMethodExecutor;

public abstract class ComposedMethodsFactory<T extends IMethodMap> {

	public static class InvalidClassException extends RuntimeException {
		private static final long serialVersionUID = 5722017683388067641L;

		private InvalidClassException() {
			super();
		}

		private InvalidClassException(Throwable cause) {
			super(cause);
		}
	}

	private final Map<Class<?>, T> classes = Maps.newHashMap();

	private final Set<Class<?>> invalidClasses = Sets.newHashSet();

	private final AdapterRegistry adapters;

	private final ClassMethodsComposer composer;

	public ComposedMethodsFactory(AdapterRegistry adapters, Predicate<IMethodExecutor> selector) {
		this.adapters = adapters;
		this.composer = new ClassMethodsComposer(selector);
	}

	public Map<Class<?>, T> listCollectedClasses() {
		return Collections.unmodifiableMap(classes);
	}

	public T getAdaptedClass(Class<?> targetCls) {
		if (invalidClasses.contains(targetCls)) throw new InvalidClassException();

		T value = classes.get(targetCls);
		if (value == null) {
			try {
				Map<String, IMethodExecutor> methods = composer.createMethodsList(targetCls, adapters);
				value = wrapMethods(targetCls, methods);
			} catch (Throwable t) {
				invalidClasses.add(targetCls);
				throw new InvalidClassException(t);
			}

			classes.put(targetCls, value);
		}

		return value;
	}

	protected abstract T wrapMethods(Class<?> targetCls, Map<String, IMethodExecutor> methods);
}
