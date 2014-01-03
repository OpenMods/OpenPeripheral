package openperipheral.integration.vanilla;

import dan200.computer.api.IComputerAccess;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;

public class AdapterFluidTank implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IFluidTank.class;
	}
	
	@LuaMethod(description="Returns info containing the capacity of the tank and the FluidStack it holds.", returnType=LuaType.TABLE)
	public FluidTankInfo getInfo(IComputerAccess computer, IFluidTank tank) {
		return tank.getInfo();
	}
}
