package openperipheral.adapter.wrappers;

import com.google.common.base.Optional;
import java.util.Set;
import openperipheral.adapter.AnnotationMetaExtractor;
import openperipheral.adapter.IMethodCall;
import openperipheral.adapter.IMethodCaller;
import openperipheral.adapter.IMethodDescription;
import openperipheral.adapter.RestrictedMethodExecutor;

public class MethodExecutorBase extends RestrictedMethodExecutor {

	private final IMethodDescription methodDescription;

	private final IMethodCaller methodCaller;

	private final boolean isAsynchronous;

	private final Optional<String> returnSignal;

	public MethodExecutorBase(IMethodDescription methodDescription, IMethodCaller methodCaller, AnnotationMetaExtractor.Bound info) {
		super(info.getExcludedArchitectures(), info.getFeatureGroups());
		this.methodDescription = methodDescription;
		this.methodCaller = methodCaller;
		this.isAsynchronous = info.isAsync();
		this.returnSignal = info.getReturnSignal();
	}

	@Override
	public IMethodDescription description() {
		return methodDescription;
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
	public Set<Class<?>> requiredEnv() {
		return methodCaller.requiredEnvArgs();
	}

	@Override
	public IMethodCall startCall(Object target) {
		return methodCaller.startCall(target);
	}

}
