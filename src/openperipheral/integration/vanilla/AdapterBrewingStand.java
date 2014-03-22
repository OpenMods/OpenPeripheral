package openperipheral.integration.vanilla;

import net.minecraft.tileentity.TileEntityBrewingStand;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class AdapterBrewingStand implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return TileEntityBrewingStand.class;
	}

	@LuaMethod
	public int getBrewTime(IComputerAccess computer, TileEntityBrewingStand brewingStand) {
		return brewingStand.getBrewTime();
	}

}
