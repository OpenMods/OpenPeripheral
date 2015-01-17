package openperipheral.converter.wrappers;

import java.lang.reflect.Method;

import openperipheral.adapter.IMethodCall;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.method.MethodDeclaration;
import openperipheral.api.IAdapter;
import openperipheral.api.IAdapterWithConstraints;

public class ExternalAdapterWrapper extends AdapterWrapper {

	public static class WithConstraints extends ExternalAdapterWrapper {

		private final IAdapterWithConstraints adapter;

		public WithConstraints(IAdapterWithConstraints adapter) {
			super(adapter);
			this.adapter = adapter;
		}

		@Override
		public boolean canUse(Class<?> cls) {
			return adapter.canApply(cls);
		}

		@Override
		public String describe() {
			return "external object (w/ constraints) (source: " + adapterClass.toString() + ")";
		}
	}

	private final IAdapter adapter;

	public ExternalAdapterWrapper(IAdapter adapter) {
		super(adapter.getClass(), adapter.getTargetClass(), adapter.getSourceId());
		this.adapter = adapter;
	}

	@Override
	public boolean canUse(Class<?> cls) {
		return true;
	}

	@Override
	public String describe() {
		return "external (source: " + adapterClass.toString() + ")";
	}

	@Override
	protected void verifyArguments(MethodDeclaration decl) {
		decl.validatePositionalNames(targetClass);
	}

	@Override
	public IMethodExecutor createExecutor(Method method, MethodDeclaration decl) {
		return new MethodExecutorBase(decl, asyncChecker.isAsync(method)) {
			@Override
			public IMethodCall startCall(Object target) {
				return method.startCall(adapter).setPositionalArg(0, target);
			}
		};
	}
}
