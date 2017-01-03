package openperipheral.adapter.wrappers;

import java.lang.reflect.Method;
import java.util.List;
import openmods.Log;
import openperipheral.adapter.IMethodCall;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.method.MethodDeclaration;
import openperipheral.adapter.property.PropertyListBuilder;

public class InlineAdapterWrapper extends AdapterWrapper {

	public InlineAdapterWrapper(Class<?> rootClass, Class<?> targetClass, String source) {
		super(targetClass, targetClass, rootClass, source);
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
	protected void prepareDeclaration(MethodDeclaration decl) {}

	@Override
	public IMethodExecutor createExecutor(Method method, MethodDeclaration decl) {
		return new MethodExecutorBase(decl, method, metaInfo) {
			@Override
			public IMethodCall startCall(Object target) {
				return super.startCall(target);
			}
		};
	}

	@Override
	protected List<IMethodExecutor> buildMethodList() {
		List<IMethodExecutor> result = super.buildMethodList();

		// non-fatal to avoid sideness annoyances
		try {
			PropertyListBuilder.buildPropertyList(rootClass, targetClass, source, metaInfo, result);
		} catch (Exception e) {
			Log.warn(e, "Failed to get properties for class %s, skipping", targetClass);
		}

		return result;
	}

}
