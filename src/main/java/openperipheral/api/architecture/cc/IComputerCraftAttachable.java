package openperipheral.api.architecture.cc;

import dan200.computercraft.api.peripheral.IComputerAccess;
import openperipheral.api.architecture.IAttachable;

/**
 * Tile Entities marked with this annotation will be informed when computer is attached.
 * This class is for ComputerCraft only. For generic interface use {@link IAttachable}
 */
public interface IComputerCraftAttachable {
	public void addComputer(IComputerAccess machine);

	public void removeComputer(IComputerAccess machine);
}
