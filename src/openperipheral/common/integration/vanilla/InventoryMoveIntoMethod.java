package openperipheral.common.integration.vanilla;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import openperipheral.api.IRestriction;
import openperipheral.common.util.InventoryUtils;
import openperipheral.api.IMethodDefinition;

public class InventoryMoveIntoMethod implements IMethodDefinition {

	private String name;
	private boolean pull;

	public InventoryMoveIntoMethod(String name, boolean pull) {
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
		return new Class[] { ForgeDirection.class, int.class, int.class, int.class };
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
	public Object execute(TileEntity tile, Object[] args) throws Exception {
		int merged = 0;
		if (tile instanceof IInventory) {
			ForgeDirection direction = (ForgeDirection) args[0];
			int slot = (Integer) args[1];
			int maxAmount = (Integer) args[2];
			int intoSlot = (Integer) args[3];
			if (direction == ForgeDirection.UNKNOWN) {
				return 0;
			}

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
			InventoryUtils.tryMergeStacks(targetInventory, intoSlot, clonedStack);
			merged = (amountToMerge - clonedStack.stackSize);
			invent.decrStackSize(slot, merged);

		}
		return merged;
	}

}
