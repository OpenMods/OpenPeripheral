package openperipheral.integration.vanilla;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import openmods.utils.InventoryUtils;
import openperipheral.api.*;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

@OnTick
@Prefixed("target")
public class AdapterInventory implements IPeripheralAdapter {

	private static final int ANY_SLOT = -1;

	public AdapterInventory() {}

	@Override
	public Class<?> getTargetClass() {
		return IInventory.class;
	}

	@LuaMethod(returnType = LuaType.STRING, description = "Get the name of this inventory")
	public String getInventoryName(IInventory target) {
		IInventory inventory = InventoryUtils.getInventory(target);
		if (inventory == null) return null;
		return inventory.getInvName();
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Get the size of this inventory")
	public int getInventorySize(IInventory target) {
		IInventory inventory = InventoryUtils.getInventory(target);
		if (inventory == null) return 0;
		return inventory.getSizeInventory();
	}

	private static void checkSlotId(IInventory inventory, int slot, String name) {
		Preconditions.checkNotNull(inventory, "Invalid inventory");
		if (slot != ANY_SLOT) Preconditions.checkElementIndex(slot, inventory.getSizeInventory(), name + " slot id");
	}

	@Alias("pullItemIntoSlot")
	@LuaCallable(returnTypes = LuaType.NUMBER, description = "Pull an item from a slot in another inventory into a slot in this one. Returns the amount of items moved")
	public int pullItem(IInventory target,
			@Arg(type = LuaType.STRING, name = "direction", description = "The direction of the other inventory. (north, south, east, west, up or down)") ForgeDirection direction,
			@Arg(type = LuaType.NUMBER, name = "slot", description = "The slot in the OTHER inventory that you're pulling from") int fromSlot,
			@Optionals @Arg(type = LuaType.NUMBER, name = "maxAmount", description = "The maximum amount of items you want to pull") Integer maxAmount,
			@Arg(type = LuaType.NUMBER, name = "intoSlot", description = "The slot in the current inventory that you want to pull into") Integer intoSlot) {

		Preconditions.checkArgument(direction != null && direction != ForgeDirection.UNKNOWN, "Invalid direction");
		TileEntity te = (TileEntity)target;

		final IInventory otherInventory = InventoryUtils.getInventory(te.worldObj, te.xCoord, te.yCoord, te.zCoord, direction);
		final IInventory thisInventory = InventoryUtils.getInventory(target);

		if (otherInventory == null || otherInventory == target) return 0;
		if (maxAmount == null) maxAmount = 64;
		if (intoSlot == null) intoSlot = 0;

		fromSlot -= 1;
		intoSlot -= 1;

		checkSlotId(otherInventory, fromSlot, "input");
		checkSlotId(thisInventory, intoSlot, "output");

		return InventoryUtils.moveItemInto(otherInventory, fromSlot, thisInventory, intoSlot, maxAmount, direction.getOpposite(), true);
	}

	@Alias("pushItemIntoSlot")
	@LuaCallable(returnTypes = LuaType.NUMBER, description = "Push an item from the current inventory into slot on the other one. Returns the amount of items moved")
	public int pushItem(IInventory target,
			@Arg(type = LuaType.STRING, name = "direction", description = "The direction of the other inventory. (north, south, east, west, up or down)") ForgeDirection direction,
			@Arg(type = LuaType.NUMBER, name = "slot", description = "The slot in the current inventory that you're pushing from") int fromSlot,
			@Optionals @Arg(type = LuaType.NUMBER, name = "maxAmount", description = "The maximum amount of items you want to push") Integer maxAmount,
			@Arg(type = LuaType.NUMBER, name = "intoSlot", description = "The slot in the other inventory that you want to push into") Integer intoSlot) {
		Preconditions.checkArgument(direction != null && direction != ForgeDirection.UNKNOWN, "Invalid direction");
		TileEntity te = (TileEntity)target;

		final IInventory otherInventory = InventoryUtils.getInventory(te.worldObj, te.xCoord, te.yCoord, te.zCoord, direction);
		final IInventory thisInventory = InventoryUtils.getInventory(target);

		if (otherInventory == null || otherInventory == target) return 0;
		if (maxAmount == null) maxAmount = 64;
		if (intoSlot == null) intoSlot = 0;

		fromSlot -= 1;
		intoSlot -= 1;

		checkSlotId(thisInventory, fromSlot, "input");
		checkSlotId(otherInventory, intoSlot, "output");

		return InventoryUtils.moveItemInto(thisInventory, fromSlot, otherInventory, intoSlot, maxAmount, direction, true);
	}

	@LuaMethod(returnType = LuaType.VOID, description = "Condense and tidy the stacks in an inventory")
	public void condenseItems(IInventory target) {
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
			InventoryUtils.insertItemIntoInventory(inventory, stack, ForgeDirection.UNKNOWN, ANY_SLOT);
		}
	}

	@LuaCallable(description = "Swap two slots in the inventory")
	public void swapStacks(IInventory target,
			@Arg(type = LuaType.NUMBER, name = "from", description = "The first slot") int fromSlot,
			@Arg(type = LuaType.NUMBER, name = "to", description = "The other slot") int intoSlot,
			@Optionals @Arg(type = LuaType.STRING, name = "fromDirection") ForgeDirection fromDirection,
			@Arg(type = LuaType.STRING, name = "fromDirection") ForgeDirection toDirection) {
		IInventory inventory = InventoryUtils.getInventory(target);
		Preconditions.checkNotNull(inventory, "Invalid target!");
		if (inventory instanceof ISidedInventory) {
			InventoryUtils.swapStacks((ISidedInventory)inventory,
					fromSlot - 1, Objects.firstNonNull(fromDirection, ForgeDirection.UNKNOWN),
					intoSlot - 1, Objects.firstNonNull(toDirection, ForgeDirection.UNKNOWN));
		}
		else InventoryUtils.swapStacks(inventory, fromSlot - 1, intoSlot - 1);
	}

	@LuaMethod(returnType = LuaType.TABLE, description = "Get details of an item in a particular slot",
			args = {
					@Arg(type = LuaType.NUMBER, name = "slotNumber", description = "The slot number, from 1 to the max amount of slots")
			})
	public ItemStack getStackInSlot(IInventory target, int slot) {
		IInventory invent = InventoryUtils.getInventory(target);
		slot -= 1;
		Preconditions.checkElementIndex(slot, target.getSizeInventory(), "slot id");
		return invent.getStackInSlot(slot);
	}

	@LuaMethod(returnType = LuaType.TABLE, description = "Get a table with all the items of the chest")
	public ItemStack[] getAllStacks(IInventory target) {
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
	public void destroyStack(IInventory target, int slot) {
		IInventory invent = InventoryUtils.getInventory(target);
		slot -= 1;
		Preconditions.checkElementIndex(slot, target.getSizeInventory(), "slot id");
		invent.setInventorySlotContents(slot, null);
	}
}
