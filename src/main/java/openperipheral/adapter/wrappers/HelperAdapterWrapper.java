package openperipheral.adapter.wrappers;

import openperipheral.adapter.IMethodCaller;
import openperipheral.adapter.method.MethodWrapperBuilder;

public class HelperAdapterWrapper extends AdapterWrapper {

	public HelperAdapterWrapper(final Object adapter, final Class<?> targetClass, String source) {
		super(adapter.getClass(), targetClass, adapter.getClass(), source, createExecutorFactory(adapter, targetClass));
	}

	private static MethodCallerFactory createExecutorFactory(final Object adapter, final Class<?> targetClass) {
		return new MethodCallerFactory() {
			@Override
			public IMethodCaller createCaller(MethodWrapperBuilder decl) {
				decl.tryDefineTargetArg(0, targetClass);
				return decl.createBoundMethodCaller(adapter);
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
