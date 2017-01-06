package openperipheral.adapter.wrappers;

import java.util.List;
import openmods.Log;
import openperipheral.adapter.AnnotationMetaExtractor;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.method.MethodWrapperBuilder;
import openperipheral.adapter.property.PropertyListBuilder;

public class InlineAdapterWrapper extends AdapterWrapper {

	public InlineAdapterWrapper(final Class<?> rootClass, Class<?> targetClass, String source) {
		super(targetClass, targetClass, rootClass, source, createExecutorFactory(rootClass));
	}

	private static ExecutorFactory createExecutorFactory(final Class<?> rootClass) {
		return new ExecutorFactory() {
			@Override
			public IMethodExecutor createExecutor(AnnotationMetaExtractor.Bound metaInfo, MethodWrapperBuilder decl) {
				return new MethodExecutorBase(decl.getMethodDescription(), decl.createUnboundMethodCaller(), metaInfo);
			}
		};
	}

	@Override
	public boolean canUse(Class<?> cls) {
		return true;
	}

	@Override
	public String describe() {
		return "internal (source: " + adapterClass.toString() + ")";
	}

	@Override
	protected List<IMethodExecutor> buildMethodList(Class<?> rootClass, AnnotationMetaExtractor metaInfo, ExecutorFactory executorFactory) {
		List<IMethodExecutor> result = super.buildMethodList(rootClass, metaInfo, executorFactory);

		// non-fatal to avoid sideness annoyances
		try {
			PropertyListBuilder.buildPropertyList(rootClass, targetClass, source, metaInfo, result);
		} catch (Exception e) {
			Log.warn(e, "Failed to get properties for class %s, skipping", targetClass);
		}

		return result;
	}

}
