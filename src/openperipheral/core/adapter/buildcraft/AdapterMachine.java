package openperipheral.core.adapter.buildcraft;

import buildcraft.core.IMachine;
import dan200.computer.api.IComputerAccess;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;

public class AdapterMachine implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		// TODO Auto-generated method stub
		return IMachine.class;
	}
	
	@LuaMethod(description="Checks if the machine is running.", returnType=LuaType.BOOLEAN)
	public boolean isActive(IComputerAccess computer, IMachine action) {
		return action.isActive();
	}
}
