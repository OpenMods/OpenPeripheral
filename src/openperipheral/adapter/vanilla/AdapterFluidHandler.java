package openperipheral.adapter.vanilla;

import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import dan200.computer.api.IComputerAccess;

public class AdapterFluidHandler implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return IFluidHandler.class;
	}

	@LuaMethod(returnType = LuaType.TABLE, onTick = false, description = "A table of tanks will be returned, each with a table of information",
			args = {
			@Arg(type = LuaType.STRING, name = "direction", description = "The internal direction of the tank. If you're not sure, use 'unknown' (north, south, east, west, up, down or unknown)")
	})
	public FluidTankInfo[] getTankInfo(IComputerAccess computer, IFluidHandler fluidHandler, ForgeDirection direction) {
		return fluidHandler.getTankInfo(direction);
	}

}
