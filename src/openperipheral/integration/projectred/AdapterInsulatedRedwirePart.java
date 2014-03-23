package openperipheral.integration.projectred;

import mrtjp.projectred.transmission.IInsulatedRedwirePart;
import openperipheral.api.*;

public class AdapterInsulatedRedwirePart implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IInsulatedRedwirePart.class;
	}

	@Alias("getInsulatedColor")
	@LuaMethod(description = "Gets the colour of the cable.", returnType = LuaType.NUMBER)
	public int getInsulatedColour(IInsulatedRedwirePart cable) {
		return cable.getInsulatedColour();
	}

}
