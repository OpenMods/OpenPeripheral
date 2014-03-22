package openperipheral.api;

import dan200.computercraft.api.peripheral.IComputerAccess;

/**
 * Tile Entities marked with this annotation will be informed when computer is attached
 */
public interface IAttachable {
	public void addComputer(IComputerAccess computer);

	public void removeComputer(IComputerAccess computer);
}
