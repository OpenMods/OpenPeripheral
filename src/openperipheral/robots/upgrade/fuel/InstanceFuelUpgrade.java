package openperipheral.robots.upgrade.fuel;

import java.util.HashMap;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.LuaMethod;
import openperipheral.core.util.RobotUtils;

public class InstanceFuelUpgrade implements IRobotUpgradeInstance {

	private IRobot robot;
	
	public InstanceFuelUpgrade(IRobot robot) {
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
	public void update() {
	}

	@Override
	public void onTierChanged(int tier) {
		
	}
	
	@LuaMethod
	public float getFuelLevel() {
		return robot.getFuelLevel();
	}
	
	@LuaMethod
	public boolean refuel(int slot, int maxAmount) {
		IInventory inventory = robot.getInventory();
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
