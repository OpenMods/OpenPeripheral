package openperipheral.api.architecture.oc;

import li.cil.oc.api.network.Node;
import openperipheral.api.architecture.IAttachable;

/**
 * Tile Entities marked with this annotation will be informed when computer is attached.
 * This class is for OpenComputers only. For generic interface use {@link IAttachable}
 */
public interface IOpenComputersAttachable {
	public void onConnect(Node node);

	public void onDisconnect(Node node);
}
