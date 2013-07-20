package openperipheral.core.adapter.buildcraft;

import net.minecraftforge.common.ForgeDirection;
import dan200.computer.api.IComputerAccess;
import buildcraft.api.power.IPowerReceptor;
import openperipheral.api.IPeripheralAdapter;

public class AdapterPowerReceptor implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return IPowerReceptor.class;
	}
	
	public float getActivationEnergy(IComputerAccess computer, IPowerReceptor powerReceptor, ForgeDirection direction){
		return powerReceptor.getPowerReceiver(direction).getActivationEnergy();
	}
	
	public float getMJStored(IComputerAccess computer, IPowerReceptor powerReceptor, ForgeDirection direction){
		return powerReceptor.getPowerReceiver(direction).getEnergyStored();
	}
	
	public float getMaxMJReceived(IComputerAccess computer, IPowerReceptor powerReceptor, ForgeDirection direction){
		return powerReceptor.getPowerReceiver(direction).getMaxEnergyReceived();
	}
	
	public float getMaxMJStored(IComputerAccess computer, IPowerReceptor powerReceptor, ForgeDirection direction){
		return powerReceptor.getPowerReceiver(direction).getMaxEnergyStored();
	}
	
	public float getMinMJReceived(IComputerAccess computer, IPowerReceptor powerReceptor, ForgeDirection direction){
		return powerReceptor.getPowerReceiver(direction).getMinEnergyReceived();
	}

}
