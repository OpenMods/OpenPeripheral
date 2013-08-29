package openperipheral.robots.upgrade.inventory;

import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import openperipheral.api.Arg;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.core.adapter.vanilla.AdapterInventory;
import openperipheral.core.util.BlockUtils;
import openperipheral.core.util.InventoryUtils;
import dan200.computer.api.IComputerAccess;

public class AdapterInventoryUpgrade extends AdapterInventory implements IRobotUpgradeAdapter {

	private IRobot robot;
	private int tier;

	public AdapterInventoryUpgrade(IRobot robot, int tier) {
		this.robot = robot;
		this.tier = tier;
	}

	public IRobot getRobot() {
		return robot;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		// TODO Auto-generated method stub

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
		this.tier = tier;
	}

	@LuaMethod
	public boolean suck(IComputerAccess computer, IRobot robot) {
		World worldObj = robot.getWorld();
		Vec3 location = robot.getLocation();
		IInventory inventory = robot.getInventory();

		List<EntityItem> entities = worldObj.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getAABBPool().getAABB(location.xCoord - 2, location.yCoord - 2, location.zCoord - 2, location.xCoord + 3, location.yCoord + 3, location.zCoord + 3));

		for (EntityItem entity : entities) {

			if (entity.isDead) {
				continue;
			}

			ItemStack stack = entity.getEntityItem();

			if (stack != null) {

				InventoryUtils.insertItemIntoInventory(inventory, stack);
				if (stack.stackSize == 0) {
					entity.setDead();
				}
			}
		}
		return true;
	}

	@LuaMethod(
			description = "Drop items on the floor",
			args = { @Arg(
					type = LuaType.NUMBER,
					name = "slot"), @Arg(
					type = LuaType.NUMBER,
					name = "maxAmount") })
	public boolean drop(IComputerAccess computer, IRobot robot, int slot, int maxAmount) throws Exception {
		IInventory inventory = robot.getInventory();
		Vec3 location = robot.getLocation();
		slot--;
		if (slot < 0 || slot >= inventory.getSizeInventory()) { throw new Exception("Invalid slot specified"); }
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
