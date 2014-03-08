package openperipheral.integration.buildcraft;

import net.minecraftforge.common.ForgeDirection;
import openperipheral.api.*;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import dan200.computer.api.IComputerAccess;

public class AdapterPowerReceptor implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IPowerReceptor.class;
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Get the activation MJ for this block",
			args = {
					@Arg(name = "direction", type = LuaType.STRING, description = "The side of the block that you're interested in") })
	public Float getActivationEnergy(IComputerAccess computer, IPowerReceptor powerReceptor, ForgeDirection direction) {
		PowerReceiver powerReceiver = powerReceptor.getPowerReceiver(direction);
		if (powerReceiver == null) { return null; }
		return powerReceiver.getActivationEnergy();
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Get the MJ stored for this block",
			args = {
					@Arg(name = "direction", type = LuaType.STRING, description = "The side of the block that you're interested in") })
	public Float getMJStored(IComputerAccess computer, IPowerReceptor powerReceptor, ForgeDirection direction) {
		PowerReceiver powerReceiver = powerReceptor.getPowerReceiver(direction);
		if (powerReceiver == null) { return null; }
		return powerReceiver.getEnergyStored();
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Get the max MJ received",
			args = {
					@Arg(name = "direction", type = LuaType.STRING, description = "The side of the block that you're interested in") })
	public Float getMaxMJReceived(IComputerAccess computer, IPowerReceptor powerReceptor, ForgeDirection direction) {
		PowerReceiver powerReceiver = powerReceptor.getPowerReceiver(direction);
		if (powerReceiver == null) { return null; }
		return powerReceiver.getMaxEnergyReceived();
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Get the max MJ stored",
			args = {
					@Arg(name = "direction", type = LuaType.STRING, description = "The side of the block that you're interested in") })
	public Float getMaxMJStored(IComputerAccess computer, IPowerReceptor powerReceptor, ForgeDirection direction) {
		PowerReceiver powerReceiver = powerReceptor.getPowerReceiver(direction);
		if (powerReceiver == null) { return null; }
		return powerReceiver.getMaxEnergyStored();
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Get the min MJ received",
			args = {
					@Arg(name = "direction", type = LuaType.STRING, description = "The side of the block that you're interested in") })
	public Float getMinMJReceived(IComputerAccess computer, IPowerReceptor powerReceptor, ForgeDirection direction) {
		PowerReceiver powerReceiver = powerReceptor.getPowerReceiver(direction);
		if (powerReceiver == null) { return null; }
		return powerReceiver.getMinEnergyReceived();
	}

}
