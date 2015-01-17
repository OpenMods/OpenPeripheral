package openperipheral.adapter;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import openperipheral.adapter.composed.ClassMethodsComposer;

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

	public final AdapterRegistry adapters;

	public ComposedMethodsFactory(AdapterRegistry adapters) {
		this.adapters = adapters;
	}

	public Map<Class<?>, Map<String, IMethodExecutor>> listCollectedClasses() {
		return Collections.unmodifiableMap(classes);
	}

	public Map<String, IMethodExecutor> getAdaptedClass(Class<?> targetCls) {
		if (invalidClasses.contains(targetCls)) throw new InvalidClassException();

		Map<String, IMethodExecutor> value = classes.get(targetCls);
		if (value == null) {
			try {
				value = new ClassMethodsComposer().createMethodsList(targetCls, adapters);
			} catch (Throwable t) {
				invalidClasses.add(targetCls);
				throw new InvalidClassException(t);
			}

			classes.put(targetCls, value);
		}

		return value;
	}
}
