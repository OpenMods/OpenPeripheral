package openperipheral.core.adapter.projectred;

import dan200.computer.api.IComputerAccess;
import mrtjp.projectred.transmission.IBundledCablePart;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.api.Arg;

public class AdapterBundledCablePart implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IBundledCablePart.class;
	}

	@LuaMethod(description="gets the signals from the cable.", returnType=LuaType.TABLE)
	public byte[] getBundledSignal(IComputerAccess computer, IBundledCablePart cable) {
		return cable.getBundledSignal();
	}

	@LuaMethod(description="Sets the signals of the cable.", args={
			@Arg(name="signals", type=LuaType.TABLE, description="The signals of the cable.")
	})
	public void setSignal(IComputerAccess computer, IBundledCablePart cable, byte[] signal) {
		cable.setSignal(signal);
	}

}
