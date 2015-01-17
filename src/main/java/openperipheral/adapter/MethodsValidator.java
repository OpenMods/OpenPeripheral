package openperipheral.adapter;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class MethodsValidator {

	private final AdapterRegistry registry;

	public MethodsValidator(AdapterRegistry registry) {
		this.registry = registry;
	}

	private final Map<String, Class<?>> args = Maps.newHashMap();

	public void addArg(String name, Class<?> cls) {
		Class<?> prev = args.put(name, cls);
		Preconditions.checkState(prev == null, "Trying to replace type of argument %s from %s to %s", name, prev, cls);
	}

	public void validate() {
		registry.validateExternalAdapters(args);
		registry.validateInlineAdapters(args);
	}
}
