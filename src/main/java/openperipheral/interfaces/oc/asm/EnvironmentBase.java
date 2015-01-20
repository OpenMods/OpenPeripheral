package openperipheral.interfaces.oc.asm;

import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.prefab.ManagedEnvironment;
import openperipheral.adapter.DefaultEnvArgs;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.api.Architectures;
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
	}

	protected static Object[] call(Object target, IMethodExecutor executor, Context context, Arguments arguments) throws Exception {
		Object[] args = arguments.toArray();
		return DefaultEnvArgs.addCommonArgs(executor.startCall(target), Architectures.OPEN_COMPUTERS)
				.setOptionalArg(DefaultEnvArgs.ARG_CONTEXT, context)
				.call(args);
	}

	@Override
	public String preferredName() {
		return type;
	}

	@Override
	public int priority() {
		return -1; // TODO: DriverPeripheral is at 0, but we can blacklist
	}

}
