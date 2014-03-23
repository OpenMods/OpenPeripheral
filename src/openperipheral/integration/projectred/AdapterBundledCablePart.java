package openperipheral.integration.projectred;

import mrtjp.projectred.transmission.IBundledCablePart;
import openperipheral.api.*;

public class AdapterBundledCablePart implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IBundledCablePart.class;
	}

	@LuaMethod(description = "gets the signals from the cable.", returnType = LuaType.TABLE)
	public byte[] getBundledSignal(IBundledCablePart cable) {
		return cable.getBundledSignal();
	}

	@LuaMethod(description = "Sets the signals of the cable.", args = {
			@Arg(name = "signals", type = LuaType.TABLE, description = "The signals of the cable.")
	})
	public void setSignal(IBundledCablePart cable, byte[] signal) {
		cable.setSignal(signal);
	}

}
