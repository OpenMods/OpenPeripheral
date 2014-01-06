package openperipheral.integration.ic2;

import ic2.api.tile.IEnergyStorage;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import dan200.computer.api.IComputerAccess;

public class AdapterEnergyStorage implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IEnergyStorage.class;
	}

	@LuaMethod(onTick = false, description = "Get the EU capacity of this block", returnType = LuaType.NUMBER)
	public int getEUCapacity(IComputerAccess Computer, IEnergyStorage storage) {
		return storage.getCapacity();
	}

	@LuaMethod(onTick = false, description = "Get how much EU is stored in this block", returnType = LuaType.NUMBER)
	public int getEUStored(IComputerAccess computer, IEnergyStorage storage) {
		return storage.getStored();
	}

	@LuaMethod(onTick = false, description = "Get the EU output per tick", returnType = LuaType.NUMBER)
	public int getEUOutputPerTick(IComputerAccess computer, IEnergyStorage storage) {
		return storage.getOutput();
	}

}
