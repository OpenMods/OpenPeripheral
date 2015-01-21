package openperipheral.adapter.composed;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import openperipheral.adapter.AdapterRegistry;
import openperipheral.adapter.IMethodExecutor;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ComposedMethodsFactory {

	public static class InvalidClassException extends RuntimeException {
		private static final long serialVersionUID = 5722017683388067641L;

		private InvalidClassException() {
			super();
		}

		private InvalidClassException(Throwable cause) {
			super(cause);
		}
	}

	private final Map<Class<?>, Map<String, IMethodExecutor>> classes = Maps.newHashMap();

	private final Set<Class<?>> invalidClasses = Sets.newHashSet();

	private final AdapterRegistry adapters;

	private final ClassMethodsComposer composer;

	public ComposedMethodsFactory(AdapterRegistry adapters, Predicate<IMethodExecutor> selector) {
		this.adapters = adapters;
		this.composer = new ClassMethodsComposer(selector);
	}

	public Map<Class<?>, Map<String, IMethodExecutor>> listCollectedClasses() {
		return Collections.unmodifiableMap(classes);
	}

	public Map<String, IMethodExecutor> getAdaptedClass(Class<?> targetCls) {
		if (invalidClasses.contains(targetCls)) throw new InvalidClassException();

		Map<String, IMethodExecutor> value = classes.get(targetCls);
		if (value == null) {
			try {
				value = composer.createMethodsList(targetCls, adapters);
			} catch (Throwable t) {
				invalidClasses.add(targetCls);
				throw new InvalidClassException(t);
			}

			classes.put(targetCls, value);
		}

		return value;
	}
}
