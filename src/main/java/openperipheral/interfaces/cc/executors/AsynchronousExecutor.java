package openperipheral.interfaces.cc.executors;

import openperipheral.adapter.IMethodExecutor;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class AsynchronousExecutor extends PeripheralExecutor<Object> {

	public static final PeripheralExecutor<?> INSTANCE = new AsynchronousExecutor();

	@Override
	public Object[] execute(IMethodExecutor executor, Object target, IComputerAccess computer, ILuaContext context, Object[] args) throws Exception {
		return call(executor, target, computer, context, args);
	}

}
