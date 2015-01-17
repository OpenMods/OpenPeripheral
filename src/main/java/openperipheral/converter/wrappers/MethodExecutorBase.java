package openperipheral.converter.wrappers;

import java.util.Map;

import openperipheral.adapter.IDescriptable;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.method.MethodDeclaration;

public abstract class MethodExecutorBase implements IMethodExecutor {

	protected final MethodDeclaration method;

	private final boolean isAsynchronous;

	public MethodExecutorBase(MethodDeclaration method, boolean isAsynchronous) {
		this.method = method;
		this.isAsynchronous = isAsynchronous;
	}

	@Override
	public IDescriptable description() {
		return method;
	}

	@Override
	public void validateArgs(Map<String, Class<?>> args) {
		method.validateOptionalNames(args);
	}

	@Override
	public boolean isAsynchronous() {
		return isAsynchronous;
	}
}
