package openperipheral.integration.vanilla;

import net.minecraft.tileentity.TileEntityBrewingStand;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;

public class AdapterBrewingStand implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return TileEntityBrewingStand.class;
	}

	@LuaMethod
	public int getBrewTime(TileEntityBrewingStand brewingStand) {
		return brewingStand.getBrewTime();
	}

}
