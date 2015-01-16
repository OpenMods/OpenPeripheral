package openperipheral.adapter.peripheral;

import openperipheral.adapter.method.MethodDeclaration;
import openperipheral.adapter.method.MethodDeclaration.CallWrap;
import openperipheral.api.IAdapter;
import openperipheral.api.IAdapterWithConstraints;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class PeripheralExternalAdapterWrapper extends PeripheralAdapterWrapper {
	public static class WithConstraints extends PeripheralExternalAdapterWrapper {

		private final IAdapterWithConstraints adapter;

		public WithConstraints(IAdapterWithConstraints adapter) {
			super(adapter);
			this.adapter = adapter;
		}

		@Override
		public String describe() {
			return "external peripheral (w/ constraints) (source: " + adapterClass.toString() + ")";
		}

		@Override
		public boolean canUse(Class<?> cls) {
			return adapter.canApply(cls);
		}
	}

	private final IAdapter adapter;

	public PeripheralExternalAdapterWrapper(IAdapter adapter) {
		super(adapter.getClass(), adapter.getTargetClass(), adapter.getSourceId());
		this.adapter = adapter;
	}

	@Override
	protected IPeripheralMethodExecutor createDirectExecutor(MethodDeclaration method, ExecutionStrategy strategy) {
		return new PeripheralMethodExecutor(method, strategy) {
			@Override
			protected CallWrap createWrapper(IComputerAccess computer, ILuaContext context, Object target, Object[] luaArgs) {
				return method.createWrapper(adapter)
						.setJavaArg(ARG_COMPUTER, computer)
						.setJavaArg(ARG_TARGET, target)
						.setJavaArg(ARG_CONTEXT, context)
						.setLuaArgs(luaArgs);
			}
		};
	}

	@Override
	protected void configureJavaArguments(MethodDeclaration decl) {
		decl.setDefaultArgName(0, ARG_TARGET);

		decl.declareJavaArgType(ARG_COMPUTER, IComputerAccess.class);
		decl.declareJavaArgType(ARG_CONTEXT, ILuaContext.class);
		decl.declareJavaArgType(ARG_TARGET, targetClass);
	}

	@Override
	public String describe() {
		return "external peripheral (source: " + adapterClass.toString() + ")";
	}

	@Override
	public boolean canUse(Class<?> cls) {
		return true;
	}
}