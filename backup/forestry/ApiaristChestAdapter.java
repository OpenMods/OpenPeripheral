package openperipheral.core.integration.forestry;

import java.util.HashMap;

import dan200.computer.api.IComputerAccess;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.core.util.ReflectionHelper;

public class ApiaristChestAdapter implements IPeripheralAdapter {

	private Class klazz;
	
	public ApiaristChestAdapter() {
		klazz = ReflectionHelper.getClass("forestry.api.apiculture.IBeeHousing");
	}

	@Override
	public Class getTargetClass() {
		return klazz;
	}

	@LuaMethod
	public HashMap getBeeInfo(IComputerAccess computer, IInventory target, int slot) {
		IInventory invent = (IInventory) target;
		if (slot > 0 && slot <= invent.getSizeInventory()) {
			ItemStack beeStack = invent.getStackInSlot(slot-1);
			if (beeStack != null) {
				return BeeUtils.beeToMap(beeStack);
			}
		}
		return null;
	}
}
