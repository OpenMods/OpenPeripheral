package openperipheral.core.adapter.thermalexpansion;

import cofh.api.tileentity.IEnergyInfo;
import dan200.computer.api.IComputerAccess;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;

public class AdapterEnergyInfo implements IPeripheralAdapter{

	@Override
	public Class<?> getTargetClass() {
		return IEnergyInfo.class;
	}

	@LuaMethod(description="Gets the EnergyPerTick of the machine.", returnType = LuaType.NUMBER)
	public int getEnergyPerTick(IComputerAccess computer, IEnergyInfo tileEntity) {
		return tileEntity.getEnergyPerTick();
	}
	
	@LuaMethod(description="Gets the max EnergyPerTick of the machine.", returnType = LuaType.NUMBER)
	public int getMaxEnergyPerTick(IComputerAccess computer, IEnergyInfo tileEntity) {
		return tileEntity.getMaxEnergyPerTick();
	}
	
	@LuaMethod(description="Gets the Energy of the machine.", returnType = LuaType.NUMBER)
	public int getEnergy(IComputerAccess computer, IEnergyInfo tileEntity) {
		return tileEntity.getEnergy();
	}
	
	@LuaMethod(description="Gets the max Energy of the machine.", returnType = LuaType.NUMBER)
	public int getMaxEnergy(IComputerAccess computer, IEnergyInfo tileEntity) {
		return tileEntity.getMaxEnergy();
	}
}
