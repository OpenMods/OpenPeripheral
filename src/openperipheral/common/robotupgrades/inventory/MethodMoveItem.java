package openperipheral.common.robotupgrades.inventory;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import openperipheral.api.IRestriction;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.common.util.InventoryUtils;

public class MethodMoveItem implements IRobotMethod {

	private String name;
	private boolean fromRobot;
	
	public MethodMoveItem(String name, boolean fromRobot) {
		this.name = name;
		this.fromRobot = fromRobot;
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
		return name;
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
		
		InstanceInventoryUpgrade inventoryUpgrade = (InstanceInventoryUpgrade) instance;
		IRobot robot = inventoryUpgrade.getRobot();
		IInventory inventory = robot.getInventory();
		World world = robot.getWorld();
		
		// lua is 1-indexed, java is 0
		int slot = (Integer) args[0] - 1;
		int maxAmount = (Integer) args[1];
		
		// get what we're looking at
		MovingObjectPosition mop = robot.getLookingAt();
		
		// if we're looking at a tile and it's an inventory...
		if (mop == null || mop.typeOfHit != EnumMovingObjectType.TILE) {
			return false;
		}
		TileEntity lookingAtTile = world.getBlockTileEntity(mop.blockX, mop.blockY, mop.blockZ);
		if (!(lookingAtTile instanceof IInventory)) {
			return false;
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

}
