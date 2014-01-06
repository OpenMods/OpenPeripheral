package openperipheral.adapter.peripheral;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;

import openperipheral.adapter.MethodDeclaration;
import openperipheral.adapter.object.IObjectMethodExecutor;

import com.google.common.base.Preconditions;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;

public class PeripheralInlineAdapterWrapper extends PeripheralAdapterWrapper {

	private static class InlineMethodExecutor extends PeripheralMethodExecutor {

		public InlineMethodExecutor(MethodDeclaration method, ExecutionStrategy strategy) {
			super(method, strategy);
		}

		@Override
		protected Callable<Object[]> createWrapper(IComputerAccess computer, ILuaContext context, Object target, Object[] luaArgs) {
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
	protected IPeripheralMethodExecutor createDirectExecutor(MethodDeclaration method, ExecutionStrategy strategy) {
		return new InlineMethodExecutor(method, strategy);
	}

	@Override
	protected void nameDefaultParameters(MethodDeclaration decl) {
		decl.nameJavaArg(0, ARG_COMPUTER);
	}

	@Override
	protected void validateArgTypes(MethodDeclaration decl) {
		decl.checkJavaArgType(ARG_COMPUTER, IComputerAccess.class);
		decl.checkJavaArgType(ARG_CONTEXT, ILuaContext.class);
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
			public MethodDeclaration getWrappedMethod() {
				return executor.getWrappedMethod();
			}

			@Override
			public Object[] execute(IComputerAccess computer, ILuaContext context, Object target, Object[] args) throws Exception {
				Object executorTarget = targetProvider.invoke(target);
				return executor.execute(context, executorTarget, args);
			}
		};
	}
}