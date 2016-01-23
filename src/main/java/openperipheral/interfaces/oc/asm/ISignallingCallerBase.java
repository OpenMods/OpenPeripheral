package openperipheral.interfaces.oc.asm;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import openperipheral.adapter.IMethodExecutor;

public interface ISignallingCallerBase extends ICallerBase {
	public Object[] callSignallingAsync(Object target, IMethodExecutor executor, String signal, Context context, Arguments arguments) throws Exception;

	public Object[] callSignallingSync(Object target, IMethodExecutor executor, String signal, Context context, Arguments arguments) throws Exception;
}
