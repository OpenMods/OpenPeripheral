package openperipheral.core.adapter.vanilla;

import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import openperipheral.api.IPeripheralAdapter;
import dan200.computer.api.IComputerAccess;

public class AdapterFluidHandler implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return IFluidHandler.class;
	}
	
	public FluidTankInfo[] getTankInfo(IComputerAccess computer, IFluidHandler fluidHandler, ForgeDirection direction) {
		return fluidHandler.getTankInfo(direction);
	}

}
