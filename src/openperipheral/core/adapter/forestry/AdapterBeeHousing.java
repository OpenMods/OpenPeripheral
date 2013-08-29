package openperipheral.core.adapter.forestry;

import java.util.HashMap;

import net.minecraft.item.ItemStack;

import dan200.computer.api.IComputerAccess;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IIndividual;
import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;

public class AdapterBeeHousing implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return IBeeHousing.class;
	}
	
	@LuaMethod(returnType = LuaType.BOOLEAN, description = "Can the bees breed?")
	public boolean canBreed(IComputerAccess computer, IBeeHousing beeHousing) {
		return beeHousing.canBreed();
	}
	
	@LuaMethod(returnType = LuaType.TABLE, description = "Get the drone")
	public IIndividual getDrone(IComputerAccess computer, IBeeHousing beeHousing) {
		ItemStack drone = beeHousing.getDrone();
		if (drone != null) {
			return AlleleManager.alleleRegistry.getIndividual(drone);
		}
		return null;
	}

}
