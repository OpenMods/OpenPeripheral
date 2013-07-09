package openperipheral.core.integration.buildcraft;

import java.util.ArrayList;

import dan200.computer.api.IComputerAccess;

import buildcraft.api.power.IPowerReceptor;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.core.interfaces.IPeripheralMethodDefinition;
import openperipheral.core.util.ReflectionHelper;

public class PowerProviderAdapter implements IPeripheralAdapter {

	private Class klazz = null;
	
	private ArrayList<IPeripheralMethodDefinition> methods = new ArrayList<IPeripheralMethodDefinition>();
	
	public PowerProviderAdapter() {
		klazz = ReflectionHelper.getClass("buildcraft.api.power.IPowerReceptor");
	}
	
	@Override
	public Class getTargetClass() {
		return klazz;
	}

	@LuaMethod
	public int getActivationEnergy(IComputerAccess computer, IPowerReceptor receptor) {
		return receptor.getPowerProvider().getActivationEnergy();
	}

	@LuaMethod
	public int getLatency(IComputerAccess computer, IPowerReceptor receptor) {
		return receptor.getPowerProvider().getLatency();
	}
	
	@LuaMethod
	public int getMinEnergyReceived(IComputerAccess computer, IPowerReceptor receptor) {
		return receptor.getPowerProvider().getMinEnergyReceived();
	}
	
	@LuaMethod
	public int getMaxEnergyReceived(IComputerAccess computer, IPowerReceptor receptor) {
		return receptor.getPowerProvider().getMaxEnergyReceived();
	}
	
	@LuaMethod
	public int getMaxEnergyStored(IComputerAccess computer, IPowerReceptor receptor) {
		return receptor.getPowerProvider().getMaxEnergyStored();
	}
	
	@LuaMethod
	public float getEnergyStored(IComputerAccess computer, IPowerReceptor receptor) {
		return receptor.getPowerProvider().getEnergyStored();
	}

}
