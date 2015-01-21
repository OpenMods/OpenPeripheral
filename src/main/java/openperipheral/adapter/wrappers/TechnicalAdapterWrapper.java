package openperipheral.adapter.wrappers;

import java.lang.reflect.Method;

import openperipheral.adapter.DefaultEnvArgs;
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
		return new MethodExecutorBase(decl, method, metaInfo) {
			@Override
			public IMethodCall startCall(Object target) {
				return super.startCall(adapter).setOptionalArg(DefaultEnvArgs.ARG_TARGET, target);
			}
		};
	}
}
