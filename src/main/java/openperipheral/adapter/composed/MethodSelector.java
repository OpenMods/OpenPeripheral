package openperipheral.adapter.composed;

import java.util.Map;

import openperipheral.adapter.IMethodExecutor;
import openperipheral.api.Constants;
import openperipheral.api.architecture.IArchitecture;
import openperipheral.api.converter.IConverter;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

public class MethodSelector implements Predicate<IMethodExecutor> {

	private final Map<String, Class<?>> providedEnv = Maps.newHashMap();

	private final String architecture;

	public MethodSelector(String architecture) {
		this.architecture = architecture;
	}

	public MethodSelector addDefaultEnv() {
		providedEnv.put(Constants.ARG_CONVERTER, IConverter.class);
		providedEnv.put(Constants.ARG_ARCHITECTURE, IArchitecture.class);
		return this;
	}

	public MethodSelector addProvidedEnv(String name, Class<?> cls) {
		providedEnv.put(name, cls);
		return this;
	}

	@Override
	public boolean apply(IMethodExecutor executor) {
		if (!executor.canInclude(architecture)) return false;

		Map<String, Class<?>> requiredEnv = executor.requiredEnv();

		for (Map.Entry<String, Class<?>> e : requiredEnv.entrySet()) {
			final String name = e.getKey();
			final Class<?> required = e.getValue();
			final Class<?> provided = providedEnv.get(name);
			if (provided == null || !required.isAssignableFrom(provided)) return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return String.format("selector for %s (env: %s)", architecture, providedEnv);
	}
}
