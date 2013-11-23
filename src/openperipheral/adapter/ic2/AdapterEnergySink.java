package openperipheral.adapter.ic2;

import ic2.api.energy.tile.IEnergySink;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import dan200.computer.api.IComputerAccess;

public class AdapterEnergySink implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IEnergySink.class;
	}

	@LuaMethod(onTick = false, description = "Get the maximum safe EU input", returnType = LuaType.NUMBER)
	public int getMaxSafeEUInput(IComputerAccess computer, IEnergySink sink) {
		return sink.getMaxSafeInput();
	}

}
