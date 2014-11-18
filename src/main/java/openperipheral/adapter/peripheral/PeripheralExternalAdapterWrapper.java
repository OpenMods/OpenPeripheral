package openperipheral.adapter.peripheral;

import java.lang.reflect.Method;
import java.util.Arrays;

import openperipheral.adapter.IDescriptable;
import openperipheral.adapter.method.MethodDeclaration;
import openperipheral.adapter.method.MethodDeclaration.CallWrap;
import openperipheral.adapter.object.IObjectMethodExecutor;
import openperipheral.api.IPeripheralAdapter;

import com.google.common.base.Preconditions;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class PeripheralExternalAdapterWrapper extends PeripheralAdapterWrapper {

	private class NormalMethodExecutor extends PeripheralMethodExecutor {

		public NormalMethodExecutor(MethodDeclaration method, ExecutionStrategy strategy) {
			super(method, strategy);
		}

		@Override
		protected CallWrap createWrapper(IComputerAccess computer, ILuaContext context, Object target, Object[] luaArgs) {
			return method.createWrapper(adapter)
					.setJavaArg(ARG_COMPUTER, computer)
					.setJavaArg(ARG_TARGET, target)
					.setJavaArg(ARG_CONTEXT, context)
					.setLuaArgs(luaArgs);
		}
	}

	private final IPeripheralAdapter adapter;

	public PeripheralExternalAdapterWrapper(IPeripheralAdapter adapter) {
		super(adapter.getClass(), adapter.getTargetClass(), adapter.getSourceId());
		this.adapter = adapter;
	}

	@Override
	protected IPeripheralMethodExecutor createDirectExecutor(MethodDeclaration method, ExecutionStrategy strategy) {
		return new NormalMethodExecutor(method, strategy);
	}

	@Override
	protected void configureJavaArguments(MethodDeclaration decl) {
		decl.setDefaultArgName(0, ARG_TARGET);

		decl.declareJavaArgType(ARG_COMPUTER, IComputerAccess.class);
		decl.declareJavaArgType(ARG_CONTEXT, ILuaContext.class);
		decl.declareJavaArgType(ARG_TARGET, targetCls);
	}

	@Override
	protected IPeripheralMethodExecutor adaptObjectExecutor(final Method targetProvider, final IObjectMethodExecutor executor) {
		Preconditions.checkArgument(Arrays.equals(targetProvider.getParameterTypes(), new Class<?>[] { targetCls }));
		return new IPeripheralMethodExecutor() {

			@Override
			public boolean isGenerated() {
				return false;
			}

			@Override
			public IDescriptable getWrappedMethod() {
				return executor.getWrappedMethod();
			}

			@Override
			public Object[] execute(IComputerAccess computer, ILuaContext context, Object target, Object[] args) throws Exception {
				Object executorTarget = targetProvider.invoke(adapter, target);
				return executor.execute(context, executorTarget, args);
			}
		};
	}

	@Override
	public String describeType() {
		return "external peripheral (source: " + adapterClass.toString() + ")";
	}
}