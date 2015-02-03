package openperipheral.api.architecture.oc;

import li.cil.oc.api.machine.Value;
import li.cil.oc.api.network.ManagedEnvironment;
import openperipheral.api.IApiInterface;

/**
 * API interface for wrapping Java objects to OpenComputers structures.
 */
public interface IOpenComputersObjectsFactory extends IApiInterface {
	public Value wrapObject(Object target);

	public ManagedEnvironment createPeripheral(Object target);
}
