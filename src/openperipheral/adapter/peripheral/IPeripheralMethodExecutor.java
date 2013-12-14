package openperipheral.adapter.peripheral;

import openperipheral.adapter.IMethodExecutor;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;

public interface IPeripheralMethodExecutor extends IMethodExecutor {
	public Object[] execute(IComputerAccess computer, ILuaContext context, Object target, Object[] args) throws Exception;
}