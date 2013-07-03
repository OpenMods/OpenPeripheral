package openperipheral.common.integration.vanilla;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.inventory.IInventory;
import openperipheral.api.IRestriction;
import openperipheral.common.interfaces.IPeripheralMethodDefinition;

public class InventoryStackInSlotMethod implements IPeripheralMethodDefinition {

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
		return new Class[] { int.class };
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public String getLuaName() {
		return "getStackInSlot";
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public boolean needsSanitize() {
		return true;
	}

	@Override
	public ArrayList<IRestriction> getRestrictions(int index) {
		return null;
	}

	@Override
	public Object execute(Object target, Object[] args) throws Exception {
		if (target instanceof IInventory) {
			IInventory invent = (IInventory) target;
			int slot = (Integer) args[0];
			if (slot < 1 || slot > invent.getSizeInventory()) {
				throw new Exception("Invalid slot number");
			}
			return invent.getStackInSlot(slot-1);
		}
		return null;
	}

}
