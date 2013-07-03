package openperipheral.common.util;

import net.minecraft.item.ItemStack;
import openperipheral.OpenPeripheral;
import openperipheral.common.item.meta.MetaEnergyCell;

public class RobotUtils {

	public static final float FUEL_PER_ENERGY_CELL = 1.0f;
	
	public static boolean isValidFuel(ItemStack fuelStack) {
		return OpenPeripheral.Items.generic.isA(fuelStack, MetaEnergyCell.class);
	}

	public static float getFuelForStack(ItemStack fuelStack, int amount) {
		return isValidFuel(fuelStack) ? amount * FUEL_PER_ENERGY_CELL : 0;
	}

}
