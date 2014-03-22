package openperipheral.adapter.peripheral;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import openperipheral.adapter.*;
import openperipheral.adapter.PropertyListBuilder.FieldContext;
import openperipheral.adapter.PropertyListBuilder.IPropertyExecutorFactory;
import openperipheral.adapter.PropertyListBuilder.PropertyExecutor;
import openperipheral.adapter.method.MethodDeclaration;
import openperipheral.adapter.method.MethodDeclaration.CallWrap;
import openperipheral.adapter.object.IObjectMethodExecutor;

import com.google.common.base.Preconditions;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computer.api.ILuaContext;

public class PeripheralInlineAdapterWrapper extends PeripheralAdapterWrapper implements IPropertyExecutorFactory<IPeripheralMethodExecutor> {

	private static class InlineMethodExecutor extends PeripheralMethodExecutor {

		public InlineMethodExecutor(MethodDeclaration method, ExecutionStrategy strategy, Map<String, Method> proxyArgs) {
			super(method, strategy, proxyArgs);
		}

		@Override
		protected CallWrap createWrapper(IComputerAccess computer, ILuaContext context, Object target, Object[] luaArgs) {
			return method.createWrapper(target)
					.setJavaArg(ARG_COMPUTER, computer)
					.setJavaArg(ARG_CONTEXT, context)
					.setLuaArgs(luaArgs);
		}
	}

	public PeripheralInlineAdapterWrapper(Class<?> targetClass) {
		super(targetClass, targetClass);
	}

	@Override
	protected IPeripheralMethodExecutor createDirectExecutor(MethodDeclaration method, ExecutionStrategy strategy, Map<String, Method> proxyArgs) {
		return new InlineMethodExecutor(method, strategy, proxyArgs);
	}

	@Override
	protected void nameDefaultParameters(MethodDeclaration decl) {
		decl.nameJavaArg(0, ARG_COMPUTER);
	}

	@Override
	protected void validateArgTypes(MethodDeclaration decl) {
		decl.declareJavaArgType(ARG_COMPUTER, IComputerAccess.class);
		decl.declareJavaArgType(ARG_CONTEXT, ILuaContext.class);
	}

	@Override
	protected IPeripheralMethodExecutor adaptObjectExecutor(final Method targetProvider, final IObjectMethodExecutor executor) {
		Preconditions.checkArgument(Arrays.equals(targetProvider.getParameterTypes(), new Class<?>[] {}));
		return new IPeripheralMethodExecutor() {

			@Override
			public boolean isSynthetic() {
				return false;
			}

			@Override
			public IDescriptable getWrappedMethod() {
				return executor.getWrappedMethod();
			}

			@Override
			public Object[] execute(IComputerAccess computer, ILuaContext context, Object target, Object[] args) throws Exception {
				Object executorTarget = targetProvider.invoke(target);
				return executor.execute(context, executorTarget, args);
			}
		};
	}

	private static class PeripheralPropertyExecutor extends PropertyExecutor implements IPeripheralMethodExecutor {

		protected PeripheralPropertyExecutor(FieldContext context) {
			super(context);
		}

		@Override
		public Object[] execute(IComputerAccess computer, ILuaContext context, Object target, Object[] args) throws Exception {
			return call(target, args);
		}

	}

	@Override
	public IPeripheralMethodExecutor createExecutor(FieldContext context) {
		return new PeripheralPropertyExecutor(context);
	}

	@Override
	protected List<IPeripheralMethodExecutor> buildMethodList() {
		List<IPeripheralMethodExecutor> result = super.buildMethodList();
		PropertyListBuilder.buildPropertyList(targetCls, this, result);
		return result;
	}

}