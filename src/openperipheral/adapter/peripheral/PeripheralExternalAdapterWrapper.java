package openperipheral.adapter.peripheral;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;

import openperipheral.adapter.MethodDeclaration;
import openperipheral.adapter.object.IObjectMethodExecutor;
import openperipheral.api.IPeripheralAdapter;

import com.google.common.base.Preconditions;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;

public class PeripheralExternalAdapterWrapper extends PeripheralAdapterWrapper {

	private class NormalMethodExecutor extends PeripheralMethodExecutor {

		public NormalMethodExecutor(MethodDeclaration method, ExecutionStrategy strategy) {
			super(method, strategy);
		}

		@Override
		protected Callable<Object[]> createWrapper(IComputerAccess computer, ILuaContext context, Object target, Object[] luaArgs) {
			return method.createWrapper(adapter)
					.setJavaArg(ARG_COMPUTER, computer)
					.setJavaArg(ARG_TARGET, target)
					.setJavaArg(ARG_CONTEXT, context)
					.setLuaArgs(luaArgs);
		}
	}

	private final IPeripheralAdapter adapter;

	public PeripheralExternalAdapterWrapper(IPeripheralAdapter adapter) {
		super(adapter.getClass(), adapter.getTargetClass());
		this.adapter = adapter;
	}

	@Override
	protected IPeripheralMethodExecutor createDirectExecutor(MethodDeclaration method, ExecutionStrategy strategy) {
		return new NormalMethodExecutor(method, strategy);
	}

	@Override
	protected void nameDefaultParameters(MethodDeclaration decl) {
		decl.nameJavaArg(0, ARG_COMPUTER);
		decl.nameJavaArg(1, ARG_TARGET);
	}

	@Override
	protected void validateArgTypes(MethodDeclaration decl) {
		decl.checkJavaArgType(ARG_COMPUTER, IComputerAccess.class);
		decl.checkJavaArgType(ARG_CONTEXT, ILuaContext.class);
		decl.checkJavaArgType(ARG_TARGET, targetCls);
	}

	@Override
	protected IPeripheralMethodExecutor adaptObjectExecutor(final Method targetProvider, final IObjectMethodExecutor executor) {
		Preconditions.checkArgument(Arrays.equals(targetProvider.getParameterTypes(), new Class<?>[] { targetCls }));
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
				Object executorTarget = targetProvider.invoke(adapter, target);
				return executor.execute(context, executorTarget, args);
			}
		};
	}
}