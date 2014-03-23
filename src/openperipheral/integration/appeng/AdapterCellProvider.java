package openperipheral.integration.appeng;

import java.util.List;

import net.minecraft.item.ItemStack;
import openperipheral.api.*;
import appeng.api.IAEItemStack;
import appeng.api.IItemList;
import appeng.api.me.tiles.ICellProvider;
import appeng.api.me.util.IMEInventoryHandler;

import com.google.common.base.Preconditions;

public class AdapterCellProvider implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return ICellProvider.class;
	}

	private static IMEInventoryHandler getCell(ICellProvider provider) {
		IMEInventoryHandler cell = provider.provideCell();
		Preconditions.checkNotNull(cell, "No valid cell");
		return cell;
	}

	@LuaCallable(description = "Is block valid", returnTypes = LuaType.BOOLEAN)
	public boolean isValid(ICellProvider provider) {
		return provider.provideCell() != null;
	}

	@LuaMethod(description = "Get the total total item types stored", returnType = LuaType.NUMBER)
	public long getTotalItemTypes(ICellProvider provider) {
		return getCell(provider).getTotalItemTypes();
	}

	@LuaMethod(description = "Get the priority of this machine", returnType = LuaType.NUMBER)
	public int getPriority(ICellProvider provider) {
		return getCell(provider).getPriority();
	}

	@LuaMethod(description = "Can this machine hold any new items?", returnType = LuaType.NUMBER)
	public boolean canHoldNewItem(ICellProvider provider) {
		return getCell(provider).canHoldNewItem();
	}

	@LuaMethod(description = "Get the amount of free bytes", returnType = LuaType.NUMBER)
	public long getFreeBytes(ICellProvider provider) {
		return getCell(provider).freeBytes();
	}

	@LuaMethod(description = "Get a list of the available items", returnType = LuaType.TABLE)
	public IItemList getAvailableItems(ICellProvider provider) {
		return getCell(provider).getAvailableItems();
	}

	@LuaMethod(
			description = "Check to see if the network contains an item type",
			returnType = LuaType.BOOLEAN,
			args = {
					@Arg(type = LuaType.NUMBER, name = "itemId", description = "The item id"),
					@Arg(type = LuaType.NUMBER, name = "dmgValue", description = "The item dmg value") })
	public boolean containsItemType(ICellProvider provider, int itemId, int dmgValue) {
		return countOfItemType(provider, itemId, dmgValue) > 0;
	}

	@LuaMethod(description = "Count the amount of a certain item type", returnType = LuaType.NUMBER, args = { @Arg(type = LuaType.NUMBER, name = "itemId", description = "The item id"), @Arg(
			type = LuaType.NUMBER,
			name = "dmgValue",
			description = "The item dmg value") })
	public long countOfItemType(ICellProvider provider, int itemId, int dmgValue) {
		IMEInventoryHandler cell = getCell(provider);
		long c = 0;
		for (IAEItemStack stack : cell.getAvailableItems()) {
			if (stack.getItemID() == itemId && stack.getItemDamage() == dmgValue) {
				c += stack.getStackSize();
			}
		}
		return c;
	}

	@LuaMethod(description = "Get the name of this cell", returnType = LuaType.STRING)
	public String getName(ICellProvider provider) {
		return getCell(provider).getName();
	}

	@LuaMethod(description = "Get a list of the preformatted items", returnType = LuaType.TABLE)
	public List<ItemStack> getPreformattedItems(ICellProvider provider) {
		return getCell(provider).getPreformattedItems();
	}

	@LuaMethod(description = "Is fuzzy preformatted", returnType = LuaType.BOOLEAN)
	public boolean isFuzzyPreformatted(ICellProvider provider) {
		return getCell(provider).isFuzzyPreformatted();
	}

	@LuaMethod(description = "Is preformatted", returnType = LuaType.BOOLEAN)
	public boolean isPreformatted(ICellProvider provider) {
		return getCell(provider).isPreformatted();
	}

	@LuaMethod(description = "Get the remaining item count", returnType = LuaType.NUMBER)
	public long getRemainingItemCount(ICellProvider provider) {
		return getCell(provider).remainingItemCount();
	}

	@LuaMethod(description = "Get the remaining item type count", returnType = LuaType.NUMBER)
	public long getRemainingItemTypes(ICellProvider provider) {
		return getCell(provider).remainingItemTypes();
	}

	@LuaMethod(description = "Get the amount of stored items", returnType = LuaType.NUMBER)
	public long getStoredItemCount(ICellProvider provider) {
		return getCell(provider).storedItemCount();
	}

	@LuaMethod(description = "Get the amount of stored item types", returnType = LuaType.NUMBER)
	public long getStoredItemTypes(ICellProvider provider) {
		return getCell(provider).storedItemTypes();
	}

	@LuaMethod(description = "Get the total bytes", returnType = LuaType.NUMBER)
	public long getTotalBytes(ICellProvider provider) {
		return getCell(provider).totalBytes();
	}

	@LuaMethod(description = "Get the unused item count", returnType = LuaType.NUMBER)
	public long getUnusedItemCount(ICellProvider provider) {
		return getCell(provider).unusedItemCount();
	}

	@LuaMethod(description = "Get the unused bytes", returnType = LuaType.NUMBER)
	public long getUnusedBytes(ICellProvider provider) {
		return getCell(provider).usedBytes();
	}

}
