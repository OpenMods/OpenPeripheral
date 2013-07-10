package openperipheral.core.adapter.appeng;

import java.util.List;

import net.minecraft.item.ItemStack;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import appeng.api.IAEItemStack;
import appeng.api.IItemList;
import appeng.api.me.tiles.ICellProvider;
import dan200.computer.api.IComputerAccess;

public class AdapterCellProvider implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return ICellProvider.class;
	}

	@LuaMethod
	public long getTotalItemTypes(IComputerAccess computer,
			ICellProvider provider) {
		return provider.provideCell().getTotalItemTypes();
	}

	@LuaMethod
	public int getPriority(IComputerAccess computer, ICellProvider provider) {
		return provider.provideCell().getPriority();
	}

	@LuaMethod
	public boolean canHoldNewItem(IComputerAccess computer,
			ICellProvider provider) {
		return provider.provideCell().canHoldNewItem();
	}

	@LuaMethod
	public long getFreeBytes(IComputerAccess computer, ICellProvider provider) {
		return provider.provideCell().freeBytes();
	}

	@LuaMethod
	public IItemList getAvailableItems(IComputerAccess computer,
			ICellProvider provider) {
		return provider.provideCell().getAvailableItems();
	}

	@LuaMethod
	public boolean getAvailableItems(IComputerAccess computer,
			ICellProvider provider, IAEItemStack stack) {
		return provider.provideCell().canAccept(stack);
	}

	@LuaMethod
	public boolean containsItemType(IComputerAccess computer,
			ICellProvider provider, IAEItemStack stack) {
		return provider.provideCell().containsItemType(stack);
	}

	@LuaMethod
	public long countOfItemType(IComputerAccess computer,
			ICellProvider provider, IAEItemStack stack) {
		return provider.provideCell().countOfItemType(stack);
	}

	@LuaMethod
	public String getName(IComputerAccess computer, ICellProvider provider) {
		return provider.provideCell().getName();
	}

	@LuaMethod
	public List<ItemStack> getPreformattedItems(IComputerAccess computer,
			ICellProvider provider) {
		return provider.provideCell().getPreformattedItems();
	}

	@LuaMethod
	public boolean isFuzzyPreformatted(IComputerAccess computer,
			ICellProvider provider) {
		return provider.provideCell().isFuzzyPreformatted();
	}

	@LuaMethod
	public boolean isPreformatted(IComputerAccess computer,
			ICellProvider provider) {
		return provider.provideCell().isPreformatted();
	}

	@LuaMethod
	public long getRemainingItemCount(IComputerAccess computer,
			ICellProvider provider) {
		return provider.provideCell().remainingItemCount();
	}

	@LuaMethod
	public long getRemainingItemTypes(IComputerAccess computer,
			ICellProvider provider) {
		return provider.provideCell().remainingItemTypes();
	}

	@LuaMethod
	public long getStoredItemCount(IComputerAccess computer,
			ICellProvider provider) {
		return provider.provideCell().storedItemCount();
	}

	@LuaMethod
	public long getStoredItemTypes(IComputerAccess computer,
			ICellProvider provider) {
		return provider.provideCell().storedItemTypes();
	}

	@LuaMethod
	public long getTotalBytes(IComputerAccess computer, ICellProvider provider) {
		return provider.provideCell().totalBytes();
	}

	@LuaMethod
	public long getUnusedItemCount(IComputerAccess computer,
			ICellProvider provider) {
		return provider.provideCell().unusedItemCount();
	}

	@LuaMethod
	public long getUnusedBytes(IComputerAccess computer, ICellProvider provider) {
		return provider.provideCell().usedBytes();
	}
}
