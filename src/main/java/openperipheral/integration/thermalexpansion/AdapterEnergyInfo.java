package openperipheral.integration.thermalexpansion;

import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import cofh.api.tileentity.IEnergyInfo;

public class AdapterEnergyInfo implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IEnergyInfo.class;
	}

	@LuaMethod(description = "Gets the EnergyPerTick of the machine.", returnType = LuaType.NUMBER)
	public int getEnergyPerTick(IEnergyInfo tileEntity) {
		return tileEntity.getEnergyPerTick();
	}

	@LuaMethod(description = "Gets the max EnergyPerTick of the machine.", returnType = LuaType.NUMBER)
	public int getMaxEnergyPerTick(IEnergyInfo tileEntity) {
		return tileEntity.getMaxEnergyPerTick();
	}

	@LuaMethod(description = "Gets the Energy of the machine.", returnType = LuaType.NUMBER)
	public int getEnergy(IEnergyInfo tileEntity) {
		return tileEntity.getEnergy();
	}

	@LuaMethod(description = "Gets the max Energy of the machine.", returnType = LuaType.NUMBER)
	public int getMaxEnergy(IEnergyInfo tileEntity) {
		return tileEntity.getMaxEnergy();
	}
}
