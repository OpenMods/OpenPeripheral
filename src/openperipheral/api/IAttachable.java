package openperipheral.api;

import dan200.computer.api.IComputerAccess;

/**
 * Tile Entities marked with this annotation will be informed when computer is attached
 */
public interface IAttachable {
	public void addComputer(IComputerAccess computer);

	public void removeComputer(IComputerAccess computer);
}
