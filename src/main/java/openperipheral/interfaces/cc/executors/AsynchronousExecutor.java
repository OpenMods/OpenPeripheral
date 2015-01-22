package openperipheral.interfaces.cc.executors;

import openperipheral.adapter.AdapterLogicException;
import openperipheral.adapter.IMethodExecutor;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class AsynchronousExecutor extends PeripheralExecutor<Object> {

	public static final PeripheralExecutor<?> INSTANCE = new AsynchronousExecutor();

	@Override
	public Object[] execute(IMethodExecutor executor, Object target, IComputerAccess computer, ILuaContext context, Object[] args) throws Exception {
		try {
			return call(executor, target, computer, context, args);
		} catch (InterruptedException e) {
			throw e;
		} catch (LuaException e) {
			throw e;
		} catch (Exception e) {
			throw new AdapterLogicException(e);
		}
	}

}
