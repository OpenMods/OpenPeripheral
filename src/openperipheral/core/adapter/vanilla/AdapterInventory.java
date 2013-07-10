package openperipheral.core.adapter.vanilla;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.core.util.BlockUtils;
import openperipheral.core.util.InventoryUtils;
import dan200.computer.api.IComputerAccess;

public class AdapterInventory implements IPeripheralAdapter {

	public AdapterInventory() {
	}
	
	@Override
	public Class getTargetClass() {
		return IInventory.class;
	}
	
	@LuaMethod
	public String getInventoryName(IComputerAccess computer, IInventory target) {
		return target.getInvName();
	}
	
	@LuaMethod
	public int getInventorySize(IComputerAccess computer, IInventory target) {
		return target.getSizeInventory();
	}

	@LuaMethod
	public int pullIntoSlot(IComputerAccess computer, IInventory target, ForgeDirection direction, int slot, int maxAmount, int intoSlot) throws Exception {
		int merged = 0;
		if (target instanceof TileEntity) {
			TileEntity tile = (TileEntity) target;
			if (direction == ForgeDirection.UNKNOWN) {
				return 0;
			}
			TileEntity targetTile = BlockUtils.getTileInDirection(tile, direction);
			if (targetTile == null || !(targetTile instanceof IInventory)) {
				throw new Exception("Target direction is not a valid inventory");
			}
			merged = InventoryUtils.moveItemInto((IInventory) targetTile, slot-1, (IInventory) tile, intoSlot-1, maxAmount);
		}
		return merged;
	}

	@LuaMethod
	public int pushIntoSlot(IComputerAccess computer, IInventory target, ForgeDirection direction, int slot, int maxAmount, int intoSlot) throws Exception {
		int merged = 0;
		boolean pull = true;
		if (target instanceof TileEntity) {
			TileEntity tile = (TileEntity) target;
			if (direction == ForgeDirection.UNKNOWN) {
				return 0;
			}
			TileEntity targetTile = BlockUtils.getTileInDirection(tile, direction);
			if (targetTile == null || !(targetTile instanceof IInventory)) {
				throw new Exception("Target direction is not a valid inventory");
			}
			merged = InventoryUtils.moveItemInto((IInventory) tile, slot-1, (IInventory) targetTile, intoSlot-1, maxAmount);
			
		}
		return merged;
	}
	
	@LuaMethod
	public int push(IComputerAccess computer, IInventory target, ForgeDirection direction, int slot, int maxAmount) throws Exception {
		int merged = 0;
		if (target instanceof TileEntity) {
			TileEntity tile = (TileEntity) target;
			if (direction == ForgeDirection.UNKNOWN) {
				return 0;
			}
			TileEntity targetTile = BlockUtils.getTileInDirection(tile, direction);
			if (targetTile == null || !(targetTile instanceof IInventory)) {
				throw new Exception("Target direction is not a valid inventory");
			}
			merged = InventoryUtils.moveItem((IInventory) tile, slot-1, (IInventory) targetTile, maxAmount);
		}
		return merged;
	}
	
	@LuaMethod
	public int pull(IComputerAccess computer, IInventory target, ForgeDirection direction, int slot, int maxAmount) throws Exception {
		int merged = 0;
		if (target instanceof TileEntity) {
			TileEntity tile = (TileEntity) target;
			if (direction == ForgeDirection.UNKNOWN) {
				return 0;
			}
			TileEntity targetTile = BlockUtils.getTileInDirection(tile, direction);
			if (targetTile == null || !(targetTile instanceof IInventory)) {
				throw new Exception("Target direction is not a valid inventory");
			}
			merged = InventoryUtils.moveItem((IInventory) targetTile, slot-1, (IInventory) tile, maxAmount);
		}
		return merged;
	}
	
	@LuaMethod
	public Object condense(IComputerAccess computer, IInventory target) throws Exception {
		IInventory invent = (IInventory) target;
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		for (int i = 0; i < invent.getSizeInventory(); i++) {
			ItemStack sta = invent.getStackInSlot(i);
			if (sta != null) {
				stacks.add(sta.copy());
			}
			invent.setInventorySlotContents(i, null);
		}
		for (ItemStack stack : stacks) {
			InventoryUtils.insertItemIntoInventory(invent, stack);
		}
		return true;	
	}
	
	@LuaMethod
	public boolean swapStacks(IComputerAccess computer, IInventory target, int from, int to) throws Exception {
		from--;
		to--;
		if (from >= 0 && from < target.getSizeInventory() && to >= 0 && to < target.getSizeInventory()) {
			
			ItemStack stack1 = target.getStackInSlot(from);
			ItemStack stack2 = target.getStackInSlot(to);
			
			if (stack1 != null) {
				stack1 = stack1.copy();
			}
			if (stack2 != null) {
				stack2 = stack2.copy();
			}
			
			target.setInventorySlotContents(from, stack2);
			target.setInventorySlotContents(to, stack1);
			return true;
		}
		return false;
	}
	
	@LuaMethod
	public ItemStack getStackInSlot(IComputerAccess computer, IInventory target, int slot) throws Exception {
		IInventory invent = (IInventory) target;
		slot--;
		if (slot < 0 || slot >= invent.getSizeInventory()) {
			throw new Exception("Invalid slot number");
		}
		return invent.getStackInSlot(slot);
	}
}
