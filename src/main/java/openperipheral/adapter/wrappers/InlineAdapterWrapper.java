package openperipheral.adapter.wrappers;

import java.util.List;
import openmods.Log;
import openperipheral.adapter.AnnotationMetaExtractor;
import openperipheral.adapter.IMethodCaller;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.method.MethodWrapperBuilder;
import openperipheral.adapter.property.PropertyListBuilder;

public class InlineAdapterWrapper extends AdapterWrapper {

	public InlineAdapterWrapper(final Class<?> rootClass, Class<?> targetClass, String source) {
		super(targetClass, targetClass, rootClass, source, createExecutorFactory(rootClass));
	}

	private static MethodCallerFactory createExecutorFactory(final Class<?> rootClass) {
		return new MethodCallerFactory() {
			@Override
			public IMethodCaller createCaller(MethodWrapperBuilder decl) {
				return decl.createUnboundMethodCaller();
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
	protected List<IMethodExecutor> buildMethodList(Class<?> rootClass, AnnotationMetaExtractor metaInfo, MethodCallerFactory executorFactory) {
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
