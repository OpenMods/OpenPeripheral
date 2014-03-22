package openperipheral.api;

import dan200.computercraft.api.peripheral.IComputerAccess;

public interface IAttachable {
	public void addComputer(IComputerAccess computer);

	public void removeComputer(IComputerAccess computer);
}
