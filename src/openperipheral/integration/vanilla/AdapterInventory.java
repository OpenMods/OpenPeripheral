package openperipheral.integration.vanilla;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import openmods.utils.InventoryUtils;
import openperipheral.api.*;

import com.google.common.base.Preconditions;

import dan200.computer.api.IComputerAccess;

public class AdapterInventory implements IPeripheralAdapter {

	public AdapterInventory() {}

	@Override
	public Class<?> getTargetClass() {
		return IInventory.class;
	}

	@LuaMethod(returnType = LuaType.STRING, description = "Get the name of this inventory")
	public String getInventoryName(IComputerAccess computer, IInventory target) {
		IInventory inventory = InventoryUtils.getInventory(target);
		if (inventory == null) return null;
		return inventory.getInvName();
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Get the size of this inventory")
	public int getInventorySize(IComputerAccess computer, IInventory target) {
		IInventory inventory = InventoryUtils.getInventory(target);
		if (inventory == null) return 0;
		return inventory.getSizeInventory();
	}

	@OnTick
	@Freeform
	@Alias("pullItemIntoSlot")
	@LuaCallable(returnTypes = LuaType.NUMBER, description = "Pull an item from a slot in another inventory into a slot in this one. Returns the amount of items moved")
	public int pullItem(@Named("target") IInventory target,
			@Arg(type = LuaType.STRING, name = "direction", description = "The direction of the other inventory. (north, south, east, west, up or down)") ForgeDirection direction,
			@Arg(type = LuaType.NUMBER, name = "slot", description = "The slot in the OTHER inventory that you're pulling from") int slot,
			@Optionals @Arg(type = LuaType.NUMBER, name = "maxAmount", description = "The maximum amount of items you want to pull") Integer maxAmount,
			@Arg(type = LuaType.NUMBER, name = "intoSlot", description = "The slot in the current inventory that you want to pull into") Integer intoSlot) {

		Preconditions.checkArgument(direction != null && direction != ForgeDirection.UNKNOWN, "Invalid direction");
		TileEntity te = (TileEntity)target;
		IInventory otherInventory = InventoryUtils.getInventory(te.worldObj, te.xCoord, te.yCoord, te.zCoord, direction);
		if (otherInventory == null || otherInventory == target) return 0;
		if (maxAmount == null) maxAmount = 64;
		if (intoSlot == null) intoSlot = 0;
		return InventoryUtils.moveItemInto(otherInventory, slot - 1, InventoryUtils.getInventory(target), intoSlot - 1, maxAmount, direction.getOpposite(), true);
	}

	@OnTick
	@Freeform
	@Alias("pushItemIntoSlot")
	@LuaCallable(returnTypes = LuaType.NUMBER, description = "Push an item from the current inventory into slot on the other one. Returns the amount of items moved")
	public int pushItem(@Named("target") IInventory target,
			@Arg(type = LuaType.STRING, name = "direction", description = "The direction of the other inventory. (north, south, east, west, up or down)") ForgeDirection direction,
			@Arg(type = LuaType.NUMBER, name = "slot", description = "The slot in the current inventory that you're pushing from") int slot,
			@Optionals @Arg(type = LuaType.NUMBER, name = "maxAmount", description = "The maximum amount of items you want to push") Integer maxAmount,
			@Arg(type = LuaType.NUMBER, name = "intoSlot", description = "The slot in the other inventory that you want to push into") Integer intoSlot) {
		Preconditions.checkArgument(direction != null && direction != ForgeDirection.UNKNOWN, "Invalid direction");
		TileEntity te = (TileEntity)target;
		IInventory otherInventory = InventoryUtils.getInventory(te.worldObj, te.xCoord, te.yCoord, te.zCoord, direction);
		if (otherInventory == null || otherInventory == target) return 0;
		if (maxAmount == null) maxAmount = 64;
		if (intoSlot == null) intoSlot = 0;
		return InventoryUtils.moveItemInto(InventoryUtils.getInventory(target), slot - 1, otherInventory, intoSlot - 1, maxAmount, direction, true);
	}

	@LuaMethod(returnType = LuaType.VOID, description = "Condense and tidy the stacks in an inventory")
	public void condenseItems(IComputerAccess computer, IInventory target) {
		IInventory inventory = InventoryUtils.getInventory(target);
		if (inventory == null && target != null) {
			// I hope to never see this ever. -NC
			System.out.println("OpenPeripheral Warning: (condenseItems) getInventory for the same inventory failed hard. That's a bug!!!");
			inventory = target;
		}
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack sta = inventory.getStackInSlot(i);
			if (sta != null) {
				stacks.add(sta.copy());
			}
			inventory.setInventorySlotContents(i, null);
		}
		for (ItemStack stack : stacks) {
			InventoryUtils.insertItemIntoInventory(inventory, stack, ForgeDirection.UNKNOWN, -1);
		}
	}

	@LuaMethod(returnType = LuaType.BOOLEAN, description = "Swap two slots in the inventory",
			args = {
					@Arg(type = LuaType.NUMBER, name = "from", description = "The first slot"),
					@Arg(type = LuaType.NUMBER, name = "to", description = "The other slot")
			})
	public boolean swapStacks(IComputerAccess computer, IInventory target, int from, int to) {
		return swapStacks(target, from - 1, to - 1);
	}

	public static boolean swapStacks(IInventory target, int from, int to) {
		IInventory inventory = InventoryUtils.getInventory(target);
		Preconditions.checkNotNull(inventory, "Invalid target!");
		if (from >= 0 && from < inventory.getSizeInventory() && to >= 0 && to < inventory.getSizeInventory()) {

			ItemStack stack1 = inventory.getStackInSlot(from);
			ItemStack stack2 = inventory.getStackInSlot(to);

			if (stack1 != null) stack1 = stack1.copy();
			if (stack2 != null) stack2 = stack2.copy();

			inventory.setInventorySlotContents(from, stack2);
			inventory.setInventorySlotContents(to, stack1);
			return true;
		}
		return false;
	}

	@LuaMethod(returnType = LuaType.TABLE, description = "Get details of an item in a particular slot",
			args = {
					@Arg(type = LuaType.NUMBER, name = "slotNumber", description = "The slot number, from 1 to the max amount of slots")
			})
	public ItemStack getStackInSlot(IComputerAccess computer, IInventory target, int slot) throws Exception {
		IInventory invent = InventoryUtils.getInventory(target);
		slot--;
		if (slot < 0 || slot >= invent.getSizeInventory()) { throw new Exception("Invalid slot number"); }
		return invent.getStackInSlot(slot);
	}

	@LuaMethod(returnType = LuaType.TABLE, description = "Get a table with all the items of the chest")
	public ItemStack[] getAllStacks(IComputerAccess computer, IInventory target) {
		IInventory inventory = InventoryUtils.getInventory(target);
		ItemStack[] allStacks = new ItemStack[inventory.getSizeInventory()];
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			allStacks[i] = inventory.getStackInSlot(i);
		}
		return allStacks;
	}

	@LuaMethod(returnType = LuaType.VOID, description = "Destroy a stack",
			args = {
					@Arg(type = LuaType.NUMBER, name = "slotNumber", description = "The slot number, from 1 to the max amount of slots")
			})
	public void destroyStack(IComputerAccess computer, IInventory target, int slot) throws Exception {
		IInventory invent = InventoryUtils.getInventory(target);
		slot--;
		if (slot < 0 || slot >= invent.getSizeInventory()) { throw new Exception("Invalid slot number"); }
		invent.setInventorySlotContents(slot, null);
	}

}
