package openperipheral.integration.buildcraft;

import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import buildcraft.core.IMachine;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class AdapterMachine implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IMachine.class;
	}

	@LuaMethod(description = "Checks if the machine is running.", returnType = LuaType.BOOLEAN)
	public boolean isActive(IComputerAccess computer, IMachine action) {
		return action.isActive();
	}
}
