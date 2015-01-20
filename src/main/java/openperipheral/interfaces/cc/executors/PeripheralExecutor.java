package openperipheral.interfaces.cc.executors;

import openperipheral.adapter.DefaultEnvArgs;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.api.Architectures;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;

public abstract class PeripheralExecutor<T> {
	protected final Object[] call(IMethodExecutor executor, T target, IComputerAccess computer, ILuaContext context, Object[] args) throws Exception {
		return DefaultEnvArgs.addCommonArgs(executor.startCall(target), Architectures.COMPUTER_CRAFT)
				.setOptionalArg(DefaultEnvArgs.ARG_CONTEXT, context)
				.setOptionalArg(DefaultEnvArgs.ARG_COMPUTER, computer)
				.call(args);

	}

	public abstract Object[] execute(IMethodExecutor executor, T target, IComputerAccess computer, ILuaContext context, Object[] args) throws Exception;
}
