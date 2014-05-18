package openperipheral.integration.appeng;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import openmods.utils.InventoryUtils;
import openperipheral.api.*;
import appeng.api.*;
import appeng.api.exceptions.AppEngTileMissingException;
import appeng.api.me.tiles.IGridTileEntity;
import appeng.api.me.util.IGridInterface;
import appeng.api.me.util.IMEInventoryHandler;

import com.google.common.base.Preconditions;

public class AdapterGridTileEntity implements IPeripheralAdapter {
	@Override
	public Class<?> getTargetClass() {
		return IGridTileEntity.class;
	}

	private static IGridInterface getGrid(IGridTileEntity te) {
		IGridInterface grid = te.getGrid();
		Preconditions.checkNotNull(grid, "No valid grid");
		return grid;
	}

	private static IMEInventoryHandler getCell(IGridTileEntity te) {
		IGridInterface grid = getGrid(te);
		IMEInventoryHandler cell = grid.getCellArray();
		Preconditions.checkNotNull(grid, "No valid cell");
		return cell;
	}

	@LuaMethod(description = "Request crafting of a specific item", returnType = LuaType.VOID,
			args = {
					@Arg(type = LuaType.TABLE, name = "stack", description = "A table representing the item stack") })
	public void requestCrafting(IGridTileEntity te, ItemStack stack) throws AppEngTileMissingException {
		getGrid(te).craftingRequest(stack, true, true);
	}

	@LuaMethod(description = "Extract an item", returnType = LuaType.NUMBER,
			args = {
					@Arg(type = LuaType.TABLE, name = "stack", description = "A table representing the item stack"),
					@Arg(type = LuaType.STRING, name = "direction", description = "The direction of the chest relative to the wrapped peripheral") })
	public long extractItem(IGridTileEntity te, ItemStack stack, ForgeDirection direction) {
		IMEInventoryHandler cell = getCell(te);

		WorldCoord coord = te.getLocation();
		IInventory targetInventory = InventoryUtils.getInventory(te.getWorld(), coord.x, coord.y, coord.z, direction);
		Preconditions.checkNotNull(targetInventory, "Target inventory does not exists");

		IAEItemStack request = Util.createItemStack(stack);
		Preconditions.checkState(request != null && request.getItem() != null, "Invalid item");

		IAEItemStack returned = cell.extractItems(request);
		Preconditions.checkState(returned != null, "No item found");

		int requestAmount = stack.stackSize;

		ItemStack returnedStack = returned.getItemStack();
		InventoryUtils.insertItemIntoInventory(targetInventory, returnedStack, direction.getOpposite(), -1);
		IAEItemStack giveBack = Util.createItemStack(returnedStack.copy());
		cell.addItems(giveBack);

		return requestAmount - giveBack.getStackSize();
	}

	@LuaMethod(description = "Insert an item back into the system", returnType = LuaType.NUMBER,
			args = {
					@Arg(type = LuaType.NUMBER, name = "slot", description = "The slot you wish to send"),
					@Arg(type = LuaType.NUMBER, name = "amount", description = "The amount you want to send"),
					@Arg(type = LuaType.STRING, name = "direction", description = "The direction of the chest relative to the wrapped peripheral") })
	public long insertItem(IGridTileEntity te, int slot, int amount, ForgeDirection direction) {
		IMEInventoryHandler cell = getCell(te);

		WorldCoord coord = te.getLocation();
		IInventory targetInventory = InventoryUtils.getInventory(te.getWorld(), coord.x, coord.y, coord.z, direction);
		Preconditions.checkNotNull(targetInventory, "Target inventory does not exists");
		slot--;
		Preconditions.checkArgument(slot >= 0 && slot < targetInventory.getSizeInventory(), "Slot is out of range");

		if (amount <= 0) return 0;

		ItemStack stack = targetInventory.getStackInSlot(slot);
		if (stack == null) { return 0; }

		amount = Math.min(amount, stack.stackSize);
		ItemStack sendStack = stack.copy();
		sendStack.stackSize = amount;
		IAEItemStack request = Util.createItemStack(sendStack);
		IAEItemStack remaining = cell.addItems(request);

		final long remainingCount = remaining != null? remaining.getStackSize() : 0;
		final long sent = amount - remainingCount;
		stack.stackSize -= sent;

		if (stack.stackSize <= 0) targetInventory.setInventorySlotContents(slot, null);
		return sent;
	}

	@LuaMethod(description = "Get the total total item types stored", returnType = LuaType.NUMBER)
	public long getTotalItemTypes(IGridTileEntity te) {
		return getCell(te).getTotalItemTypes();
	}

	@LuaMethod(description = "Get the priority of this machine", returnType = LuaType.NUMBER)
	public int getPriority(IGridTileEntity te) {
		return getCell(te).getPriority();
	}

	@LuaMethod(description = "Can this machine hold any new items?", returnType = LuaType.NUMBER)
	public boolean canHoldNewItem(IGridTileEntity te) {
		return getCell(te).canHoldNewItem();
	}

	@LuaMethod(description = "Get the amount of free bytes", returnType = LuaType.NUMBER)
	public long getFreeBytes(IGridTileEntity te) {
		return getCell(te).freeBytes();
	}

	@LuaMethod(description = "Get a list of the available items", returnType = LuaType.TABLE)
	public IItemList getAvailableItems(IGridTileEntity te) {
		return getCell(te).getAvailableItems();
	}

	@LuaMethod(
			description = "Check to see if the network contains an item type",
			returnType = LuaType.BOOLEAN,
			args = {
					@Arg(type = LuaType.NUMBER, name = "itemId", description = "The item id"),
					@Arg(type = LuaType.NUMBER, name = "dmgValue", description = "The item dmg value") })
	public boolean containsItemType(IGridTileEntity te, int itemId, int dmgValue) {
		return countOfItemType(te, itemId, dmgValue) > 0;
	}

	@LuaMethod(description = "Count the amount of a certain item type", returnType = LuaType.NUMBER,
			args = {
					@Arg(type = LuaType.NUMBER, name = "itemId", description = "The item id"),
					@Arg(type = LuaType.NUMBER, name = "dmgValue", description = "The item dmg value") })
	public long countOfItemType(IGridTileEntity te, int itemId, int dmgValue) {
		IMEInventoryHandler cell = getCell(te);
		long c = 0;
		for (IAEItemStack stack : cell.getAvailableItems()) {
			if (stack.getItemID() == itemId && stack.getItemDamage() == dmgValue) {
				c += stack.getStackSize();
			}
		}
		return c;
	}

	@LuaMethod(description = "Get a list of the preformatted items", returnType = LuaType.TABLE)
	public List<ItemStack> getPreformattedItems(IGridTileEntity te) {
		return getCell(te).getPreformattedItems();
	}

	@LuaMethod(description = "Is fuzzy preformatted", returnType = LuaType.BOOLEAN)
	public boolean isFuzzyPreformatted(IGridTileEntity te) {
		return getCell(te).isFuzzyPreformatted();
	}

	@LuaMethod(description = "Is preformatted", returnType = LuaType.BOOLEAN)
	public boolean isPreformatted(IGridTileEntity te) {
		return getCell(te).isPreformatted();
	}

	@LuaMethod(description = "Get the remaining item count", returnType = LuaType.NUMBER)
	public long getRemainingItemCount(IGridTileEntity te) {
		return getCell(te).remainingItemCount();
	}

	@LuaMethod(description = "Get the remaining item type count", returnType = LuaType.NUMBER)
	public long getRemainingItemTypes(IGridTileEntity te) {
		return getCell(te).remainingItemTypes();
	}

	@LuaMethod(description = "Get the amount of stored items", returnType = LuaType.NUMBER)
	public long getStoredItemCount(IGridTileEntity te) {
		return getCell(te).storedItemCount();
	}

	@LuaMethod(description = "Get the amount of stored item types", returnType = LuaType.NUMBER)
	public long getStoredItemTypes(IGridTileEntity te) {
		return getCell(te).storedItemTypes();
	}

	@LuaMethod(description = "Get the total bytes", returnType = LuaType.NUMBER)
	public long getTotalBytes(IGridTileEntity te) {
		return getCell(te).totalBytes();
	}

	@LuaMethod(description = "Get the unused item count", returnType = LuaType.NUMBER)
	public long getUnusedItemCount(IGridTileEntity te) {
		return getCell(te).unusedItemCount();
	}

	@LuaMethod(description = "Get the unused bytes", returnType = LuaType.NUMBER)
	public long getUnusedBytes(IGridTileEntity te) {
		return getCell(te).usedBytes();
	}

}
