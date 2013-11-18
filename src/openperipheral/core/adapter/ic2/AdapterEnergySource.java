package openperipheral.core.adapter.ic2;

import ic2.api.energy.tile.IEnergySource;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import dan200.computer.api.IComputerAccess;

public class AdapterEnergySource implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IEnergySource.class;
	}

	@LuaMethod(onTick = false, description = "Get the EU output", returnType = LuaType.NUMBER)
	public double getOfferedEnergy(IComputerAccess computer, IEnergySource source) {
		return source.getOfferedEnergy();
	}

}
