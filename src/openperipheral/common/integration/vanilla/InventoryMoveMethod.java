package openperipheral.common.integration.vanilla;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import openperipheral.api.IRestriction;
import openperipheral.common.interfaces.IPeripheralMethodDefinition;
import openperipheral.common.util.InventoryUtils;

public class InventoryMoveMethod implements IPeripheralMethodDefinition {

	private String name;
	private boolean pull;

	public InventoryMoveMethod(String name, boolean pull) {
		this.name = name;
		this.pull = pull;
	}

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
		return new Class[] { ForgeDirection.class, int.class, int.class };
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public String getLuaName() {
		return name;
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
		int merged = 0;
		if (target instanceof IInventory && target instanceof TileEntity) {
			TileEntity tile = (TileEntity) target;
			ForgeDirection direction = (ForgeDirection) args[0];
			if (direction == ForgeDirection.UNKNOWN) {
				return 0;
			}
			int slot = ((Integer) args[1]) - 1;
			int maxAmount = (Integer) args[2];

			int targetX = tile.xCoord + direction.offsetX;
			int targetY = tile.yCoord + direction.offsetY;
			int targetZ = tile.zCoord + direction.offsetZ;
			TileEntity targetTile = tile.worldObj.getBlockTileEntity(targetX, targetY, targetZ);
			if (!(targetTile instanceof IInventory)) {
				throw new Exception("Target direction is not a valid inventory");
			}
			IInventory targetInventory;
			IInventory invent;
			if (pull) {
				invent = (IInventory) targetTile;
				targetInventory = (IInventory) tile;
			} else {
				targetInventory = (IInventory) targetTile;
				invent = (IInventory) tile;
			}

			ItemStack stack = invent.getStackInSlot(slot);
			if (stack == null) {
				return 0;
			}
			ItemStack clonedStack = stack.copy();
			clonedStack.stackSize = Math.min(clonedStack.stackSize, maxAmount);
			int amountToMerge = clonedStack.stackSize;
			InventoryUtils.insertItemIntoInventory(targetInventory, clonedStack);
			merged = (amountToMerge - clonedStack.stackSize);
			invent.decrStackSize(slot, merged);
		}
		return merged;
	}
}
