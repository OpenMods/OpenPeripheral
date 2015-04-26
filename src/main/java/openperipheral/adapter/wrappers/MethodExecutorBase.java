package openperipheral.adapter.wrappers;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import openperipheral.adapter.IMethodCall;
import openperipheral.adapter.IMethodDescription;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.method.MethodDeclaration;

public abstract class MethodExecutorBase implements IMethodExecutor {

	private final MethodDeclaration decl;

	private final boolean isAsynchronous;

	private final Set<String> excludedArchitectures;

	public MethodExecutorBase(MethodDeclaration decl, Method method, MethodMetaExtractor info) {
		this.decl = decl;
		this.isAsynchronous = info.isAsync(method);
		this.excludedArchitectures = info.getExcludedArchitectures(method);
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
