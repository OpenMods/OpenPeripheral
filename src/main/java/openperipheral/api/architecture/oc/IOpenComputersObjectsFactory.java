package openperipheral.api.architecture.oc;

import li.cil.oc.api.machine.Value;
import li.cil.oc.api.network.ManagedEnvironment;
import openperipheral.api.IApiInterface;
import openperipheral.api.adapter.GenerationFailedException;

/**
 * API interface for wrapping Java objects to OpenComputers structures.
 * It will return same objects as normally returned to OpenComputers for TileEntities and converted objects.
 */
public interface IOpenComputersObjectsFactory extends IApiInterface {

	/**
	 * @throws GenerationFailedException
	 */
	public Value wrapObject(Object target);

	/**
	 * @throws GenerationFailedException
	 */
	public ManagedEnvironment createPeripheral(Object target);
}
