package openperipheral.interfaces.oc.asm.object;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.prefab.AbstractValue;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.interfaces.oc.OpenComputersEnv;
import openperipheral.interfaces.oc.asm.ICallerBase;

import com.google.common.base.Preconditions;

public class ObjectEnvironmentBase extends AbstractValue implements ICallerBase {

	@Override
	public Object[] call(Object target, IMethodExecutor executor, Context context, Arguments arguments) throws Exception {
		Preconditions.checkArgument(target != null, "This object is no longer valid");

		Object[] args = arguments.toArray();
		return OpenComputersEnv.addCommonArgs(executor.startCall(target), context).call(args);
	}

	@Override
	public Object[] invalidState() {
		throw new IllegalStateException("Object is no longer valid");
	}

}
