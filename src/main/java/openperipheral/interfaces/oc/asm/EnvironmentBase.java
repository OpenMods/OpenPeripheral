package openperipheral.interfaces.oc.asm;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.api.Constants;
import openperipheral.interfaces.oc.OpenComputersEnv;
import openperipheral.util.NameUtils;

/**
 * Warning: don't change method names or arguments, used in {@link EnvironmentCodeGenerator}
 *
 * @author boq
 *
 */
public abstract class EnvironmentBase extends ManagedEnvironment implements NamedBlock {

	public static final String METHODS_FIELD = "methods";

	private final String type;

	public EnvironmentBase(Object target) {
		this.type = NameUtils.getNameForTarget(target);

		setNode(Network.newNode(this, Visibility.Network).
				withComponent(this.type).
				create());
	}

	protected static Object[] call(Object target, IMethodExecutor executor, Context context, Arguments arguments) throws Exception {
		Object[] args = arguments.toArray();
		return OpenComputersEnv.addPeripheralArgs(executor.startCall(target), context)
				.setOptionalArg(Constants.ARG_CONTEXT, context)
				.call(args);
	}

	@Override
	public String preferredName() {
		return type;
	}

	@Override
	public int priority() {
		return -1; // DriverPeripheral is at 0, but we blacklist OP peripherals
	}

}
