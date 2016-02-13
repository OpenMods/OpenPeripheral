package openperipheral.adapter.wrappers;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import openperipheral.adapter.*;
import openperipheral.adapter.method.MethodDeclaration;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

public abstract class MethodExecutorBase implements IMethodExecutor {

	private final MethodDeclaration decl;

	private final boolean isAsynchronous;

	private final Optional<String> returnSignal;

	private final Set<String> excludedArchitectures;

	public MethodExecutorBase(MethodDeclaration decl, Method method, AnnotationMetaExtractor info) {
		this.decl = decl;
		this.isAsynchronous = info.isAsync(method);
		this.returnSignal = info.getReturnSignal(method);
		this.excludedArchitectures = ImmutableSet.copyOf(info.getExcludedArchitectures(method));
	}

	@Override
	public IMethodDescription description() {
		return decl;
	}

	@Override
	public boolean isAsynchronous() {
		return isAsynchronous;
	}

	@Override
	public Optional<String> getReturnSignal() {
		return returnSignal;
	}

	@Override
	public boolean canInclude(String architecture) {
		return !this.excludedArchitectures.contains(architecture);
	}

	@Override
	public Map<String, Class<?>> requiredEnv() {
		return decl.getOptionalArgs();
	}

	@Override
	public IMethodCall startCall(Object target) {
		return decl.startCall(target);
	}

}
