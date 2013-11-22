package openperipheral.core.adapter.projectred;

import dan200.computer.api.IComputerAccess;
import mrtjp.projectred.transmission.IInsulatedRedwirePart;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;

public class AdapterInsulatedRedwirePart implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IInsulatedRedwirePart.class;
	}

	@LuaMethod(description="Gets the colour of the cable.", returnType=LuaType.NUMBER)
	public int getInsulatedColour(IComputerAccess computer, IInsulatedRedwirePart cable) {
		return cable.getInsulatedColour();
	}

}
