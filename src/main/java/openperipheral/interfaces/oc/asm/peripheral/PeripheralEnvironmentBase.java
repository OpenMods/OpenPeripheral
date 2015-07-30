package openperipheral.interfaces.oc.asm.peripheral;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;
import openmods.utils.CachedFactory;
import openperipheral.adapter.IMethodCall;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.PeripheralTypeProvider;
import openperipheral.api.architecture.IArchitectureAccess;
import openperipheral.api.architecture.IAttachable;
import openperipheral.api.architecture.oc.IOpenComputersAttachable;
import openperipheral.interfaces.oc.ModuleOpenComputers;
import openperipheral.interfaces.oc.asm.ICallerBase;

public class PeripheralEnvironmentBase extends ManagedEnvironment implements NamedBlock, ICallerBase {

	private final String type;

	private final CachedFactory<Context, IArchitectureAccess> accessCache = new CachedFactory<Context, IArchitectureAccess>() {
		@Override
		protected IArchitectureAccess create(Context context) {
			return ModuleOpenComputers.ENV.createAccess(node(), context);
		}
	};

	public PeripheralEnvironmentBase(Object target) {
		this.type = PeripheralTypeProvider.INSTANCE.generateType(target);

		setNode(Network.newNode(this, Visibility.Network).
				withComponent(this.type).
				create());
	}

	@Override
	public Object[] call(Object target, IMethodExecutor executor, Context context, Arguments arguments) throws Exception {
		Object[] args = arguments.toArray();
		final IMethodCall call = executor.startCall(target);
		return ModuleOpenComputers.ENV.addPeripheralArgs(call, node(), context).call(args);
	}

	protected void onConnect(IAttachable target, Node node) {
		final Environment host = node.host();
		if (host instanceof Context) {
			IArchitectureAccess access = accessCache.getOrCreate((Context)host);
			target.addComputer(access);
		}
	}

	protected void onDisconnect(IAttachable target, Node node) {
		final Environment host = node.host();
		if (host instanceof Context) {
			IArchitectureAccess access = accessCache.remove((Context)host);
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

	@Override
	public Object[] invalidState() {
		throw new IllegalStateException("Peripheral is no longer valid");
	}

}
