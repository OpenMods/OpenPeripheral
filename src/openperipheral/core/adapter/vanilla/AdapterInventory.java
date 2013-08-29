package openperipheral.core.adapter.vanilla;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.core.util.BlockUtils;
import openperipheral.core.util.InventoryUtils;
import dan200.computer.api.IComputerAccess;

public class AdapterInventory implements IPeripheralAdapter {

	public AdapterInventory() {}

	@Override
	public Class getTargetClass() {
		return IInventory.class;
	}

	@LuaMethod(
			returnType = LuaType.STRING,
			description = "Get the name of this inventory")
	public String getInventoryName(IComputerAccess computer, IInventory target) {
		return target.getInvName();
	}

	@LuaMethod(
			returnType = LuaType.NUMBER,
			description = "Get the size of this inventory")
	public int getInventorySize(IComputerAccess computer, IInventory target) {
		return target.getSizeInventory();
	}

	@LuaMethod(
			returnType = LuaType.NUMBER,
			description = "Pull an item from a slot in another inventory into a specific slot in this one. Returns the amount of items moved",
			args = { @Arg(
					type = LuaType.STRING,
					name = "direction",
					description = "The direction of the other inventory. (north, south, east, west, up or down)"), @Arg(
					type = LuaType.NUMBER,
					name = "slot",
					description = "The slot in the OTHER inventory that you're pulling from"), @Arg(
					type = LuaType.NUMBER,
					name = "maxAmount",
					description = "The maximum amount of items you want to pull"), @Arg(
					type = LuaType.NUMBER,
					name = "intoSlot",
					description = "The slot in the current inventory that you want to pull into") })
	public int pullItemIntoSlot(IComputerAccess computer, IInventory target, ForgeDirection direction, int slot, int maxAmount, int intoSlot) throws Exception {
		int merged = 0;
		if (target instanceof TileEntity) {
			TileEntity tile = (TileEntity)target;
			if (direction == ForgeDirection.UNKNOWN) { return 0; }
			TileEntity targetTile = BlockUtils.getTileInDirection(tile, direction);
			if (targetTile == null || !(targetTile instanceof IInventory)) { throw new Exception("Target direction is not a valid inventory"); }
			merged = InventoryUtils.moveItemInto((IInventory)targetTile, slot - 1, (IInventory)tile, intoSlot - 1, maxAmount);
		}
		return merged;
	}

	@LuaMethod(
			returnType = LuaType.NUMBER,
			description = "Push an item from the current inventory into a specific slot in the other one. Returns the amount of items moved",
			args = { @Arg(
					type = LuaType.STRING,
					name = "direction",
					description = "The direction of the other inventory. (north, south, east, west, up or down)"), @Arg(
					type = LuaType.NUMBER,
					name = "slot",
					description = "The slot in the current inventory that you're pushing from"), @Arg(
					type = LuaType.NUMBER,
					name = "maxAmount",
					description = "The maximum amount of items you want to push"), @Arg(
					type = LuaType.NUMBER,
					name = "intoSlot",
					description = "The slot in the other inventory that you want to push into") })
	public int pushItemIntoSlot(IComputerAccess computer, IInventory target, ForgeDirection direction, int slot, int maxAmount, int intoSlot) throws Exception {
		int merged = 0;
		boolean pull = true;
		if (target instanceof TileEntity) {
			TileEntity tile = (TileEntity)target;
			if (direction == ForgeDirection.UNKNOWN) { return 0; }
			TileEntity targetTile = BlockUtils.getTileInDirection(tile, direction);
			if (targetTile == null || !(targetTile instanceof IInventory)) { throw new Exception("Target direction is not a valid inventory"); }
			merged = InventoryUtils.moveItemInto((IInventory)tile, slot - 1, (IInventory)targetTile, intoSlot - 1, maxAmount);

		}
		return merged;
	}

	@LuaMethod(
			returnType = LuaType.NUMBER,
			description = "Push an item from the current inventory into any slot on the other one. Returns the amount of items moved",
			args = { @Arg(
					type = LuaType.STRING,
					name = "direction",
					description = "The direction of the other inventory. (north, south, east, west, up or down)"), @Arg(
					type = LuaType.NUMBER,
					name = "slot",
					description = "The slot in the current inventory that you're pushing from"), @Arg(
					type = LuaType.NUMBER,
					name = "maxAmount",
					description = "The maximum amount of items you want to push") })
	public int pushItem(IComputerAccess computer, IInventory target, ForgeDirection direction, int slot, int maxAmount) throws Exception {
		int merged = 0;
		if (target instanceof TileEntity) {
			TileEntity tile = (TileEntity)target;
			if (direction == ForgeDirection.UNKNOWN) { return 0; }
			TileEntity targetTile = BlockUtils.getTileInDirection(tile, direction);
			if (targetTile == null || !(targetTile instanceof IInventory)) { throw new Exception("Target direction is not a valid inventory"); }
			merged = InventoryUtils.moveItem((IInventory)tile, slot - 1, (IInventory)targetTile, maxAmount);
		}
		return merged;
	}

	@LuaMethod(
			returnType = LuaType.NUMBER,
			description = "Pull an item from the target inventory into any slot in the current one. Returns the amount of items moved",
			args = { @Arg(
					type = LuaType.STRING,
					name = "direction",
					description = "The direction of the other inventory. (north, south, east, west, up or down)"), @Arg(
					type = LuaType.NUMBER,
					name = "slot",
					description = "The slot in the other inventory that you're pulling from"), @Arg(
					type = LuaType.NUMBER,
					name = "maxAmount",
					description = "The maximum amount of items you want to pull") })
	public int pullItem(IComputerAccess computer, IInventory target, ForgeDirection direction, int slot, int maxAmount) throws Exception {
		int merged = 0;
		if (target instanceof TileEntity) {
			TileEntity tile = (TileEntity)target;
			if (direction == ForgeDirection.UNKNOWN) { return 0; }
			TileEntity targetTile = BlockUtils.getTileInDirection(tile, direction);
			if (targetTile == null || !(targetTile instanceof IInventory)) { throw new Exception("Target direction is not a valid inventory"); }
			merged = InventoryUtils.moveItem((IInventory)targetTile, slot - 1, (IInventory)tile, maxAmount);
		}
		return merged;
	}

	@LuaMethod(
			returnType = LuaType.VOID,
			description = "Condense and tidy the stacks in an inventory")
	public void condenseItems(IComputerAccess computer, IInventory target) throws Exception {
		IInventory invent = (IInventory)target;
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
	}

	@LuaMethod(
			returnType = LuaType.BOOLEAN,
			description = "Swap two slots in the inventory",
			args = { @Arg(
					type = LuaType.NUMBER,
					name = "from",
					description = "The first slot"), @Arg(
					type = LuaType.NUMBER,
					name = "to",
					description = "The other slot") })
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

	@LuaMethod(
			returnType = LuaType.TABLE,
			description = "Get details of an item in a particular slot",
			args = { @Arg(
					type = LuaType.NUMBER,
					name = "slotNumber",
					description = "The slot number, from 1 to the max amount of slots") })
	public ItemStack getStackInSlot(IComputerAccess computer, IInventory target, int slot) throws Exception {
		IInventory invent = (IInventory)target;
		slot--;
		if (slot < 0 || slot >= invent.getSizeInventory()) { throw new Exception("Invalid slot number"); }
		return invent.getStackInSlot(slot);
	}
}
