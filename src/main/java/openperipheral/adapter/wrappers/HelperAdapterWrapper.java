package openperipheral.adapter.wrappers;

import openperipheral.adapter.AnnotationMetaExtractor;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.method.MethodWrapperBuilder;

public class HelperAdapterWrapper extends AdapterWrapper {

	public HelperAdapterWrapper(final Object adapter, final Class<?> targetClass, String source) {
		super(adapter.getClass(), targetClass, adapter.getClass(), source, createExecutorFactory(adapter, targetClass));
	}

	private static ExecutorFactory createExecutorFactory(final Object adapter, final Class<?> targetClass) {
		return new ExecutorFactory() {
			@Override
			public IMethodExecutor createExecutor(AnnotationMetaExtractor.Bound metaInfo, MethodWrapperBuilder decl) {
				decl.tryDefineTargetArg(0, targetClass);
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
		return "generated (source: " + adapterClass.toString() + ")";
	}

}
