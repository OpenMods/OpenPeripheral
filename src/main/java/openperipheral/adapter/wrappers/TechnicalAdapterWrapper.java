package openperipheral.adapter.wrappers;

import java.lang.reflect.Method;

import openperipheral.adapter.DefaultArgNames;
import openperipheral.adapter.IMethodCall;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.method.MethodDeclaration;

public class TechnicalAdapterWrapper extends AdapterWrapper {

	private final Object adapter;

	public TechnicalAdapterWrapper(Object adapter, Class<?> targetClass, String source) {
		super(adapter.getClass(), targetClass, source);
		this.adapter = adapter;
	}

	@Override
	public boolean canUse(Class<?> cls) {
		return true;
	}

	@Override
	public String describe() {
		return "generated (source: " + adapterClass.toString() + ")";
	}

	@Override
	protected void verifyArguments(MethodDeclaration decl) {}

	@Override
	public IMethodExecutor createExecutor(Method method, MethodDeclaration decl) {
		return new MethodExecutorBase(decl, asyncChecker.isAsync(method)) {
			@Override
			public IMethodCall startCall(Object target) {
				return method.startCall(adapter).setOptionalArg(DefaultArgNames.ARG_TARGET, target);
			}
		};
	}
}
