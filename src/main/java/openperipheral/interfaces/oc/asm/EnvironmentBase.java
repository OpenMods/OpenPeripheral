package openperipheral.interfaces.oc.asm;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;
import openmods.utils.CachedFactory;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.api.Constants;
import openperipheral.api.architecture.IArchitectureAccess;
import openperipheral.api.architecture.IAttachable;
import openperipheral.api.architecture.oc.IOpenComputersAttachable;
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

	private final CachedFactory<Context, IArchitectureAccess> accessCache = new CachedFactory<Context, IArchitectureAccess>() {
		@Override
		protected IArchitectureAccess create(Context context) {
			return OpenComputersEnv.createAccess(node(), context);
		}
	};

	public EnvironmentBase(Object target) {
		this.type = NameUtils.getNameForTarget(target);

		setNode(Network.newNode(this, Visibility.Network).
				withComponent(this.type).
				create());
	}

	protected Object[] call(Object target, IMethodExecutor executor, Context context, Arguments arguments) throws Exception {
		Object[] args = arguments.toArray();
		return OpenComputersEnv.addPeripheralArgs(executor.startCall(target), node(), context)
				.setOptionalArg(Constants.ARG_CONTEXT, context)
				.call(args);
	}

	protected void onConnect(IAttachable target, Node node) {
		if (node instanceof Context) {
			IArchitectureAccess access = accessCache.getOrCreate((Context)node);
			target.addComputer(access);
		}
	}

	protected void onDisconnect(IAttachable target, Node node) {
		if (node instanceof Context) {
			IArchitectureAccess access = accessCache.remove((Context)node);
			if (access != null) target.removeComputer(access);
		}
	}

	protected static void onConnect(IOpenComputersAttachable target, Node node) {
		target.onConnect(node);
	}

	protected static void onDisconnect(IOpenComputersAttachable target, Node node) {
		target.onDisconnect(node);
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
