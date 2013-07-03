package openperipheral.common.integration.vanilla;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import openperipheral.api.IRestriction;
import openperipheral.common.interfaces.IPeripheralMethodDefinition;
import openperipheral.common.util.InventoryUtils;

public class InventoryCondenseMethod implements IPeripheralMethodDefinition {

	@Override
	public HashMap<Integer, String> getReplacements() {
		return null;
	}

	@Override
	public String getPostScript() {
		return null;
	}

	@Override
	public boolean getCauseTileUpdate() {
		return false;
	}

	@Override
	public Class[] getRequiredParameters() {
		return null;
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public String getLuaName() {
		return "condense";
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public boolean needsSanitize() {
		return false;
	}

	@Override
	public ArrayList<IRestriction> getRestrictions(int index) {
		return null;
	}

	@Override
	public Object execute(Object target, Object[] args) throws Exception {
		if (target instanceof IInventory) {
			IInventory invent = (IInventory) target;
			ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
			for (int i = 0; i < invent.getSizeInventory(); i++) {
				ItemStack sta = invent.getStackInSlot(i);
				if (sta != null) {
					stacks.add(sta.copy());
				}
				invent.setInventorySlotContents(i, null);
			}
			for (ItemStack stack : stacks) {
				InventoryUtils.insertItemIntoInventory(invent, stack);
			}
			return true;	
		}
		return false;
	}

}
