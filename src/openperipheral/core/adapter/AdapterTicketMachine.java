package openperipheral.core.adapter;

import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.core.block.TileEntityTicketMachine;
import dan200.computer.api.IComputerAccess;

public class AdapterTicketMachine implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return TileEntityTicketMachine.class;
	}

	@LuaMethod(returnType = LuaType.VOID, description = "Lock the ink and paper slot")
	public void lock(IComputerAccess computer, TileEntityTicketMachine ticketMachine) {
		ticketMachine.lock();
	}

	@LuaMethod(returnType = LuaType.VOID, description = "Unlock the ink and paper slot")
	public void unlock(IComputerAccess computer, TileEntityTicketMachine ticketMachine) {
		ticketMachine.unlock();
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "Create a new ticket to the specified destination", args = { @Arg(
		name = "destination",
		description = "The destination for the ticket",
		type = LuaType.STRING) })
	public boolean createTicket(IComputerAccess computer, TileEntityTicketMachine ticketMachine, String destination) {
		return ticketMachine.createTicket(destination);
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "Returns true if this machine is locked")
	public boolean isLocked(IComputerAccess computer, TileEntityTicketMachine ticketMachine) {
		return ticketMachine.isLocked();
	}
}
