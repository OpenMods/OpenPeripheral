package openperipheral.core.adapter.ic2;

import ic2.api.energy.tile.IEnergySink;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import dan200.computer.api.IComputerAccess;

public class AdapterEnergySink implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return IEnergySink.class;
	}

	@LuaMethod
	public int getMaxSafeEUInput(IComputerAccess computer, IEnergySink sink) {
		return sink.getMaxSafeInput();
	}

}
