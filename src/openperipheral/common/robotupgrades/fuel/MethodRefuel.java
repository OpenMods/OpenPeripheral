package openperipheral.common.robotupgrades.fuel;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import openperipheral.api.IRestriction;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.common.util.RobotUtils;

/**
 * Untested
 * @author mikeef
 *
 */
public class MethodRefuel implements IRobotMethod {

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
		return "refuel";
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public Class[] getRequiredParameters() {
		return new Class[]  { int.class, int.class };
	}

	@Override
	public Object execute(IRobotUpgradeInstance instance, Object[] args) throws Exception {
		InstanceFuelUpgrade fuelUpgrade = (InstanceFuelUpgrade) instance;
		IRobot robot = fuelUpgrade.getRobot();
		IInventory inventory = robot.getInventory();
		int slot = (Integer)args[0] - 1;
		int maxAmount = (Integer)args[1];
		if (slot < 0 || slot > inventory.getSizeInventory()-1) {
			return false;
		}
		ItemStack fuelStack = inventory.getStackInSlot(slot);
		if (fuelStack != null) {
			maxAmount = Math.min(maxAmount, fuelStack.stackSize);
			float increase = RobotUtils.getFuelForStack(fuelStack, maxAmount);
			fuelStack.stackSize -= maxAmount;
			robot.modifyFuelLevel(increase);
			if (fuelStack.stackSize > 0) {
				inventory.setInventorySlotContents(slot, fuelStack.copy());
			}else {
				inventory.setInventorySlotContents(slot, null);
			}
			return true;
		}
		return false;
	}

}
