package openperipheral.interfaces.oc.asm;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import openperipheral.adapter.IMethodExecutor;

public interface ICallerBase {
	public Object[] invalidState();

	public Object[] call(Object target, IMethodExecutor executor, Context context, Arguments arguments) throws Exception;
}
