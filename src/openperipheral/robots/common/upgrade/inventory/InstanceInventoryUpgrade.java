package openperipheral.robots.common.upgrade.inventory;

import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobot;
import openperipheral.api.LuaMethod;
import openperipheral.core.util.BlockUtils;
import openperipheral.core.util.InventoryUtils;

public class InstanceInventoryUpgrade implements IRobotUpgradeInstance {

	private IRobot robot;
	
	public InstanceInventoryUpgrade(IRobot robot) {
		this.robot = robot;
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
		// TODO Auto-generated method stub
		
	}
	
	@LuaMethod
	public int pushItem(int slot, int maxAmount) throws Exception {
		return moveItem(slot, maxAmount, true);
	}

	@LuaMethod
	public int pullItem(int slot, int maxAmount) throws Exception {
		return moveItem(slot, maxAmount, false);
	}
	
	@LuaMethod
	public int pushItemInto(int slot, int maxAmount, int targetSlot) throws Exception {
		return moveItemInto(slot, maxAmount, targetSlot, true);
	}

	@LuaMethod
	public int pullItemInto(int slot, int maxAmount, int targetSlot) throws Exception {
		return moveItemInto(slot, maxAmount, targetSlot, false);
	}
	
	@LuaMethod
	public boolean suck() {
		World worldObj = robot.getWorld();
		Vec3 location = robot.getLocation();
		IInventory inventory = robot.getInventory();

		List<EntityItem> entities = worldObj.getEntitiesWithinAABB(
				EntityItem.class,
				AxisAlignedBB.getAABBPool().getAABB(location.xCoord - 2, location.yCoord - 2, location.zCoord - 2, location.xCoord + 3, location.yCoord + 3,
						location.zCoord + 3));

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
	
	public int moveItem(int slot, int maxAmount, boolean fromRobot) throws Exception {
		IInventory inventory = robot.getInventory();
		World world = robot.getWorld();
		slot--;
		// get what we're looking at
		MovingObjectPosition mop = robot.getLookingAt();

		// if we're looking at a tile and it's an inventory...
		if (mop == null || mop.typeOfHit != EnumMovingObjectType.TILE) {
			return 0;
		}
		
		TileEntity lookingAtTile = world.getBlockTileEntity(mop.blockX, mop.blockY, mop.blockZ);
		if (!(lookingAtTile instanceof IInventory)) {
			return 0;
		}
		
		IInventory toInventory;
		IInventory fromInventory;
		if (fromRobot) {
			fromInventory = inventory;
			toInventory = (IInventory) lookingAtTile;
		} else {
			toInventory = inventory;
			fromInventory = (IInventory) lookingAtTile;
		}
		
		// if the slot if out of bounds, throw an error
		if (slot < 0 || slot > fromInventory.getSizeInventory() - 1) {
			throw new Exception("Invalid slot number specified");
		}
		
		// get the local stack
		ItemStack fromStack = fromInventory.getStackInSlot(slot);
		
		// how many items were pushed across
		int merged = 0;
		
		// if there's no stack, return false
		if (fromStack == null) {
			return merged;
		}
			
		ItemStack clonedStack = fromStack.copy();
		clonedStack.stackSize = Math.min(clonedStack.stackSize, maxAmount);
		int amountToMerge = clonedStack.stackSize;
		InventoryUtils.insertItemIntoInventory(toInventory, clonedStack);
		merged = (amountToMerge - clonedStack.stackSize);
		fromInventory.decrStackSize(slot, merged);

		return merged;
	}
	
	public int moveItemInto(int slot, int maxAmount, int intoSlot, boolean fromRobot) throws Exception {
		IInventory inventory = robot.getInventory();
		World world = robot.getWorld();
		
		slot--;
		intoSlot--;
		
		// get what we're looking at
		MovingObjectPosition mop = robot.getLookingAt();

		// if we're looking at a tile and it's an inventory...
		if (mop == null || mop.typeOfHit != EnumMovingObjectType.TILE) {
			return 0;
		}
		
		TileEntity lookingAtTile = world.getBlockTileEntity(mop.blockX, mop.blockY, mop.blockZ);
		if (!(lookingAtTile instanceof IInventory)) {
			return 0;
		}
		
		IInventory toInventory;
		IInventory fromInventory;
		if (fromRobot) {
			fromInventory = inventory;
			toInventory = (IInventory) lookingAtTile;
		} else {
			toInventory = inventory;
			fromInventory = (IInventory) lookingAtTile;
		}
		
		// if the slot if out of bounds, throw an error
		if (slot < 0 || slot > fromInventory.getSizeInventory() - 1) {
			throw new Exception("Invalid slot number specified");
		}
		
		// get the local stack
		ItemStack fromStack = fromInventory.getStackInSlot(slot);
		
		// how many items were pushed across
		int merged = 0;
		
		// if there's no stack, return false
		if (fromStack == null) {
			return merged;
		}

		ItemStack clonedStack = fromStack.copy();
		clonedStack.stackSize = Math.min(clonedStack.stackSize, maxAmount);
		int amountToMerge = clonedStack.stackSize;
		InventoryUtils.tryMergeStacks(toInventory, intoSlot, clonedStack);
		merged = (amountToMerge - clonedStack.stackSize);
		fromInventory.decrStackSize(slot, merged);

		return merged;
	}
	
	@LuaMethod
	public boolean drop(int slot, int maxAmount) throws Exception {
		IInventory inventory = robot.getInventory();
		Vec3 location = robot.getLocation();
		slot--;
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
