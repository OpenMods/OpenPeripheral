package openperipheral.adapter.object;

import openperipheral.adapter.IMethodExecutor;
import dan200.computercraft.api.lua.ILuaContext;

public interface IObjectMethodExecutor extends IMethodExecutor {
	public Object[] execute(ILuaContext context, Object target, Object[] args) throws Exception;
}