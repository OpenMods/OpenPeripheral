package openperipheral.adapter.wrappers;

import java.lang.reflect.Method;
import java.util.Map;

import openperipheral.adapter.IMethodCall;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.method.MethodDeclaration;
import openperipheral.api.Constants;
import openperipheral.api.adapter.IAdapter;
import openperipheral.api.adapter.IAdapterWithConstraints;

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
		super(adapter.getClass(), adapter.getTargetClass(), adapter.getClass(), adapter.getSourceId());
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
	protected void prepareDeclaration(MethodDeclaration decl) {
		decl.nameEnv(0, Constants.ARG_TARGET, targetClass);
	}

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
