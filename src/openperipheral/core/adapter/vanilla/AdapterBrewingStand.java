package openperipheral.core.adapter.vanilla;

import net.minecraft.tileentity.TileEntityBrewingStand;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import dan200.computer.api.IComputerAccess;

public class AdapterBrewingStand implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return TileEntityBrewingStand.class;
	}
	
	@LuaMethod
	public int getBrewTime(IComputerAccess computer, TileEntityBrewingStand brewingStand) {
		return brewingStand.getBrewTime();
	}

}
