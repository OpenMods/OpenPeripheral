package openperipheral.adapter.composed;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import java.util.Set;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.api.architecture.IArchitecture;
import openperipheral.api.converter.IConverter;

public class MethodSelector implements Predicate<IMethodExecutor> {

	private final Set<Class<?>> providedEnv = Sets.newHashSet();

	private final String architecture;

	private boolean allowReturnSignal;

	public MethodSelector(String architecture) {
		this.architecture = architecture;
	}

	public MethodSelector addDefaultEnv() {
		providedEnv.add(IConverter.class);
		providedEnv.add(IArchitecture.class);
		return this;
	}

	public MethodSelector addProvidedEnv(Class<?> cls) {
		providedEnv.add(cls);
		return this;
	}

	public MethodSelector allowReturnSignal() {
		this.allowReturnSignal = true;
		return this;
	}

	@Override
	public boolean apply(IMethodExecutor executor) {
		if (!executor.canInclude(architecture)) return false;
		if (!allowReturnSignal && executor.getReturnSignal().isPresent()) return false;
		return Sets.difference(executor.requiredEnv(), providedEnv).isEmpty();
	}

	@Override
	public String toString() {
		return String.format("selector for %s (env: %s)", architecture, providedEnv);
	}
}
