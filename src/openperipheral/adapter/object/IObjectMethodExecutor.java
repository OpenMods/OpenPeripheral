package openperipheral.adapter.object;

import openperipheral.adapter.IMethodExecutor;
import dan200.computer.api.ILuaContext;

public interface IObjectMethodExecutor extends IMethodExecutor {
	public Object[] execute(ILuaContext context, Object target, Object[] args) throws Exception;
}