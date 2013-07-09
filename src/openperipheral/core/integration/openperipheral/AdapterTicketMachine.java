package openperipheral.core.integration.openperipheral;

import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.core.block.TileEntityTicketMachine;
import dan200.computer.api.IComputerAccess;

public class AdapterTicketMachine implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return TileEntityTicketMachine.class;
	}

	@LuaMethod
	public void lock(IComputerAccess computer,
			TileEntityTicketMachine ticketMachine) {
		ticketMachine.lock();
	}

	@LuaMethod
	public void unlock(IComputerAccess computer,
			TileEntityTicketMachine ticketMachine) {
		ticketMachine.unlock();
	}

	@LuaMethod
	public boolean createTicket(IComputerAccess computer,
			TileEntityTicketMachine ticketMachine, String destination) {
		return ticketMachine.createTicket(destination);
	}

	@LuaMethod
	public boolean isLocked(IComputerAccess computer,
			TileEntityTicketMachine ticketMachine) {
		return ticketMachine.isLocked();
	}
}
