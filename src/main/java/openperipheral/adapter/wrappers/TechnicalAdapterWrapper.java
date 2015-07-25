package openperipheral.adapter.wrappers;

import java.lang.reflect.Method;
import java.util.Map;

import openperipheral.adapter.IMethodCall;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.method.MethodDeclaration;
import openperipheral.api.Constants;

public class TechnicalAdapterWrapper extends AdapterWrapper {

	private final Object adapter;

	public TechnicalAdapterWrapper(Object adapter, Class<?> targetClass, String source) {
		super(adapter.getClass(), targetClass, targetClass, source);
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
	protected void prepareDeclaration(MethodDeclaration decl) {}

	@Override
	public IMethodExecutor createExecutor(Method method, MethodDeclaration decl) {
		return new MethodExecutorBase(decl, method, metaInfo) {
			@Override
			public IMethodCall startCall(Object target) {
				return super.startCall(adapter).setEnv(Constants.ARG_TARGET, target);
			}

			@Override
			public Map<String, Class<?>> requiredEnv() {
				final Map<String, Class<?>> requiredEnv = super.requiredEnv();
				requiredEnv.remove(Constants.ARG_TARGET);
				return requiredEnv;
			}
		};
	}
}
