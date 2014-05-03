package openperipheral.integration.buildcraft;

import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import buildcraft.core.IMachine;

public class AdapterMachine implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IMachine.class;
	}

	@LuaMethod(description = "Checks if the machine is running.", returnType = LuaType.BOOLEAN)
	public boolean isActive(IMachine action) {
		return action.isActive();
	}
}
