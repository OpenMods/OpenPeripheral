package openperipheral.integration.projectred;

import mrtjp.projectred.transmission.IInsulatedRedwirePart;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class AdapterInsulatedRedwirePart implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IInsulatedRedwirePart.class;
	}

	@LuaMethod(description = "Gets the colour of the cable.", returnType = LuaType.NUMBER)
	public int getInsulatedColour(IComputerAccess computer, IInsulatedRedwirePart cable) {
		return cable.getInsulatedColour();
	}

}
