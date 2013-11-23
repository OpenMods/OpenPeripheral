package openperipheral.adapter.vanilla;

import net.minecraft.tileentity.TileEntityFurnace;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import dan200.computer.api.IComputerAccess;

public class AdapterFurnace implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return TileEntityFurnace.class;
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Number of ticks the current item will cook for")
	public int getBurnTime(IComputerAccess computer, TileEntityFurnace furnace) {
		return furnace.furnaceBurnTime;
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Number of ticks the current item has been cooking")
	public int getCookTime(IComputerAccess computer, TileEntityFurnace furnace) {
		return furnace.furnaceCookTime;
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Number of ticks the current item would take to cook")
	public int getCurrentItemBurnTime(IComputerAccess computer, TileEntityFurnace furnace) {
		return furnace.currentItemBurnTime;
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "Is the furnace currently burning?")
	public boolean isBurning(IComputerAccess computer, TileEntityFurnace furnace) {
		return furnace.isBurning();
	}
}
