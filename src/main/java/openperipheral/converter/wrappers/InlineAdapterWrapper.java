package openperipheral.converter.wrappers;

import java.lang.reflect.Method;
import java.util.List;

import openperipheral.adapter.IMethodCall;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.PropertyListBuilder;
import openperipheral.adapter.method.MethodDeclaration;

public class InlineAdapterWrapper extends AdapterWrapper {

	public InlineAdapterWrapper(Class<?> targetClass, String source) {
		super(targetClass, targetClass, source);
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
	protected void verifyArguments(MethodDeclaration decl) {}

	@Override
	public IMethodExecutor createExecutor(Method method, MethodDeclaration decl) {
		return new MethodExecutorBase(decl, asyncChecker.isAsync(method)) {
			@Override
			public IMethodCall startCall(Object target) {
				return method.startCall(target);
			}
		};
	}

	@Override
	protected List<IMethodExecutor> buildMethodList() {
		List<IMethodExecutor> result = super.buildMethodList();
		PropertyListBuilder.buildPropertyList(targetClass, source, result);
		return result;
	}

}
