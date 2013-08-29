package openperipheral.robots.upgrade.fuel;

import java.util.HashMap;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import openperipheral.api.Arg;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.core.util.RobotUtils;
import dan200.computer.api.IComputerAccess;

public class AdapterFuelUpgrade implements IRobotUpgradeAdapter {

	private IRobot robot;

	public AdapterFuelUpgrade(IRobot robot) {
		this.robot = robot;
	}

	public IRobot getRobot() {
		return robot;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setFloat("fuelLevel", robot.getFuelLevel());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		robot.setFuelLevel(nbt.getFloat("fuelLevel"));
	}

	@Override
	public HashMap<Integer, EntityAIBase> getAITasks() {
		return null;
	}

	@Override
	public void update() {}

	@Override
	public void onTierChanged(int tier) {

	}

	@LuaMethod(description = "")
	public float getFuelLevel(IComputerAccess computer, IRobot robot) {
		return robot.getFuelLevel();
	}

	@LuaMethod(description = "Refuel the robot", args = { @Arg(type = LuaType.NUMBER, name = "slot"), @Arg(type = LuaType.NUMBER, name = "maxAmount") })
	public boolean refuel(IComputerAccess computer, IRobot robot, int slot, int maxAmount) {
		IInventory inventory = robot.getInventory();
		if (slot < 0 || slot >= inventory.getSizeInventory()) { return false; }
		ItemStack fuelStack = inventory.getStackInSlot(slot);
		if (fuelStack != null) {
			maxAmount = Math.min(maxAmount, fuelStack.stackSize);
			float increase = RobotUtils.getFuelForStack(fuelStack, maxAmount);
			fuelStack.stackSize -= maxAmount;
			robot.modifyFuelLevel(increase);
			if (fuelStack.stackSize > 0) {
				inventory.setInventorySlotContents(slot, fuelStack.copy());
			} else {
				inventory.setInventorySlotContents(slot, null);
			}
			return true;
		}
		return false;
	}
}
