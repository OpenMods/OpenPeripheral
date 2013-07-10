package openperipheral.core.adapter.ic2;

import ic2.api.tile.IEnergyStorage;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import dan200.computer.api.IComputerAccess;

public class AdapterEnergyStorage implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return IEnergyStorage.class;
	}

	@LuaMethod
	public int getEUCapacity(IComputerAccess Computer, IEnergyStorage storage) {
		return storage.getCapacity();
	}
	
	@LuaMethod
	public int getEUStored(IComputerAccess computer, IEnergyStorage storage) {
		return storage.getStored();
	}
	
	@LuaMethod
	public int getEUOutputPerTick(IComputerAccess computer, IEnergyStorage storage) {
		return storage.getOutput();
	}

}
