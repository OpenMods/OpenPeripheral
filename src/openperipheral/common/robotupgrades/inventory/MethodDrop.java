package openperipheral.common.robotupgrades.inventory;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;

import openperipheral.api.IRestriction;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.common.util.BlockUtils;

public class MethodDrop implements IRobotMethod {

	public MethodDrop() {
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
	public String getLuaName() {
		return "drop";
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public Class[] getRequiredParameters() {
		return new Class[] { int.class, int.class };
	}

	@Override
	public Object execute(IRobotUpgradeInstance instance, Object[] args) throws Exception {
		InstanceInventoryUpgrade upgrade = (InstanceInventoryUpgrade) instance;
		IRobot robot = upgrade.getRobot();
		IInventory inventory = robot.getInventory();
		Vec3 location = robot.getLocation();
		int slot = (Integer) args[0] - 1;
		int maxAmount = (Integer) args[1];
		if (slot < 0 || slot > inventory.getSizeInventory()-1) {
			throw new Exception("Invalid slot specified");
		}
		ItemStack stack = inventory.getStackInSlot(slot);
		if (stack != null) {
			ItemStack clone = stack.copy();
			maxAmount = Math.min(maxAmount, stack.stackSize);
			inventory.decrStackSize(slot, maxAmount);
			clone.stackSize = maxAmount;
			BlockUtils.dropItemStackInWorld(robot.getWorld(), location.xCoord, location.yCoord, location.zCoord, clone);
			return true;
		}
		return false;
	}

}
