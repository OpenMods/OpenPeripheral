package openperipheral.core.adapter.buildcraft;

import net.minecraftforge.common.ForgeDirection;
import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import buildcraft.api.power.IPowerReceptor;
import dan200.computer.api.IComputerAccess;

public class AdapterPowerReceptor implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IPowerReceptor.class;
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Get the activation MJ for this block",
			args = {
			@Arg(name = "direction",type = LuaType.STRING, description = "The side of the block that you're interested in") })
	public float getActivationEnergy(IComputerAccess computer, IPowerReceptor powerReceptor, ForgeDirection direction) {
		return powerReceptor.getPowerReceiver(direction).getActivationEnergy();
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Get the MJ stored for this block",
			args = {
			@Arg(name = "direction", type = LuaType.STRING, description = "The side of the block that you're interested in") })
	public float getMJStored(IComputerAccess computer, IPowerReceptor powerReceptor, ForgeDirection direction) {
		return powerReceptor.getPowerReceiver(direction).getEnergyStored();
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Get the max MJ received",
			args = {
			@Arg(name = "direction", type = LuaType.STRING, description = "The side of the block that you're interested in") })
	public float getMaxMJReceived(IComputerAccess computer, IPowerReceptor powerReceptor, ForgeDirection direction) {
		return powerReceptor.getPowerReceiver(direction).getMaxEnergyReceived();
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Get the max MJ stored",
			args = {
			@Arg(name = "direction", type = LuaType.STRING, description = "The side of the block that you're interested in") })
	public float getMaxMJStored(IComputerAccess computer, IPowerReceptor powerReceptor, ForgeDirection direction) {
		return powerReceptor.getPowerReceiver(direction).getMaxEnergyStored();
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Get the min MJ received",
			args = {
			@Arg(name = "direction", type = LuaType.STRING, description = "The side of the block that you're interested in") })
	public float getMinMJReceived(IComputerAccess computer, IPowerReceptor powerReceptor, ForgeDirection direction) {
		return powerReceptor.getPowerReceiver(direction).getMinEnergyReceived();
	}

}
