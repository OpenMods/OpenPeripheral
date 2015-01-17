package openperipheral.interfaces.cc.executors;

import openperipheral.adapter.DefaultArgNames;
import openperipheral.adapter.IMethodExecutor;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;

public abstract class PeripheralExecutor<T> {
	protected final Object[] call(IMethodExecutor executor, T target, IComputerAccess computer, ILuaContext context, Object[] args) throws Exception {
		return executor.startCall(target)
				.setOptionalArg(DefaultArgNames.ARG_CONTEXT, context)
				.setOptionalArg(DefaultArgNames.ARG_COMPUTER, computer)
				.call(args);

	}

	public abstract Object[] execute(IMethodExecutor executor, T target, IComputerAccess computer, ILuaContext context, Object[] args) throws Exception;
}
