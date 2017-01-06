package openperipheral.adapter.wrappers;

import openperipheral.adapter.AnnotationMetaExtractor;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.method.MethodWrapperBuilder;
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

	public ExternalAdapterWrapper(final IAdapter adapter) {
		super(adapter.getClass(), adapter.getTargetClass(), adapter.getClass(), adapter.getSourceId(),
				createExecutionFactory(adapter));
	}

	private static ExecutorFactory createExecutionFactory(final IAdapter adapter) {
		return new ExecutorFactory() {
			@Override
			public IMethodExecutor createExecutor(AnnotationMetaExtractor.Bound metaInfo, MethodWrapperBuilder decl) {
				decl.defineTargetArg(0, adapter.getTargetClass());
				return new MethodExecutorBase(decl.getMethodDescription(), decl.createBoundMethodCaller(adapter), metaInfo);
			}
		};
	}

	@Override
	public boolean canUse(Class<?> cls) {
		return true;
	}

	@Override
	public String describe() {
		return "external (source: " + adapterClass.toString() + ")";
	}
}
