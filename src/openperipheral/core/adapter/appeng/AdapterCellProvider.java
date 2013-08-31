package openperipheral.core.adapter.appeng;

import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import appeng.api.IAEItemStack;
import appeng.api.IItemList;
import appeng.api.me.tiles.ICellProvider;
import appeng.api.me.util.IMEInventoryHandler;
import dan200.computer.api.IComputerAccess;

public class AdapterCellProvider implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return ICellProvider.class;
	}

	@LuaMethod(description = "Get the total total item types stored", returnType = LuaType.NUMBER)
	public long getTotalItemTypes(IComputerAccess computer, ICellProvider provider) {
		IMEInventoryHandler cell = provider.provideCell();
		if (cell != null) {
			return cell.getTotalItemTypes();
		}
		return 0;
	}

	@LuaMethod(description = "Get the priority of this machine", returnType = LuaType.NUMBER)
	public int getPriority(IComputerAccess computer, ICellProvider provider) {
		IMEInventoryHandler cell = provider.provideCell();
		if (cell != null) {
			return cell.getPriority();
		}
		return 0;
	}

	@LuaMethod(description = "Can this machine hold any new items?", returnType = LuaType.NUMBER)
	public boolean canHoldNewItem(IComputerAccess computer, ICellProvider provider) {
		IMEInventoryHandler cell = provider.provideCell();
		if (cell != null) {
			return cell.canHoldNewItem();
		}
		return false;
	}

	@LuaMethod(description = "Get the amount of free bytes", returnType = LuaType.NUMBER)
	public long getFreeBytes(IComputerAccess computer, ICellProvider provider) {
		IMEInventoryHandler cell = provider.provideCell();
		if (cell != null) {
			return cell.freeBytes();
		}
		return 0;
	}

	@LuaMethod(description = "Get a list of the available items", returnType = LuaType.TABLE)
	public IItemList getAvailableItems(IComputerAccess computer, ICellProvider provider) {
		IMEInventoryHandler cell = provider.provideCell();
		if (cell != null) {
			return cell.getAvailableItems();
		}
		return null;
	}

	@LuaMethod(
		description = "Check to see if the network contains an item type",
		returnType = LuaType.BOOLEAN,
		args = {
			@Arg(type = LuaType.NUMBER, name = "itemId", description = "The item id"),
			@Arg(type = LuaType.NUMBER, name = "dmgValue", description = "The item dmg value") })
	public boolean containsItemType(IComputerAccess computer, ICellProvider provider, int itemId, int dmgValue) {
		return countOfItemType(computer, provider, itemId, dmgValue) > 0;
	}

	@LuaMethod(description = "Count the amount of a certain item type", returnType = LuaType.NUMBER, args = { @Arg(type = LuaType.NUMBER, name = "itemId", description = "The item id"), @Arg(
		type = LuaType.NUMBER,
		name = "dmgValue",
		description = "The item dmg value") })
	public long countOfItemType(IComputerAccess computer, ICellProvider provider, int itemId, int dmgValue) {
		IMEInventoryHandler cell = provider.provideCell();
		if (cell == null) {
			return 0;
		}
		Iterator<IAEItemStack> iterator = cell.getAvailableItems().iterator();
		long c = 0;
		while (iterator.hasNext()) {
			IAEItemStack next = iterator.next();
			if (next.getItemID() == itemId && next.getItemDamage() == dmgValue) {
				c += next.getStackSize();
			}
		}
		return c;
	}

	@LuaMethod(description = "Get the name of this cell", returnType = LuaType.STRING)
	public String getName(IComputerAccess computer, ICellProvider provider) {
		IMEInventoryHandler cell = provider.provideCell();
		if (cell != null) {
			return cell.getName();
		}
		return "";
	}

	@LuaMethod(description = "Get a list of the preformatted items", returnType = LuaType.TABLE)
	public List<ItemStack> getPreformattedItems(IComputerAccess computer, ICellProvider provider) {
		IMEInventoryHandler cell = provider.provideCell();
		if (cell != null) {
			return cell.getPreformattedItems();
		}
		return null;
	}

	@LuaMethod(description = "Is fuzzy preformatted", returnType = LuaType.BOOLEAN)
	public boolean isFuzzyPreformatted(IComputerAccess computer, ICellProvider provider) {
		IMEInventoryHandler cell = provider.provideCell();
		if (cell != null) {
			return cell.isFuzzyPreformatted();
		}
		return false;
	}

	@LuaMethod(description = "Is preformatted", returnType = LuaType.BOOLEAN)
	public boolean isPreformatted(IComputerAccess computer, ICellProvider provider) {
		IMEInventoryHandler cell = provider.provideCell();
		if (cell != null) {
			return cell.isPreformatted();
		}
		return false;
	}

	@LuaMethod(description = "Get the remaining item count", returnType = LuaType.NUMBER)
	public long getRemainingItemCount(IComputerAccess computer, ICellProvider provider) {
		IMEInventoryHandler cell = provider.provideCell();
		if (cell != null) {
			return cell.remainingItemCount();
		}
		return 0;
	}

	@LuaMethod(description = "Get the remaining item type count", returnType = LuaType.NUMBER)
	public long getRemainingItemTypes(IComputerAccess computer, ICellProvider provider) {
		IMEInventoryHandler cell = provider.provideCell();
		if (cell != null) {
			return cell.remainingItemTypes();
		}
		return 0;
	}

	@LuaMethod(description = "Get the amount of stored items", returnType = LuaType.NUMBER)
	public long getStoredItemCount(IComputerAccess computer, ICellProvider provider) {
		IMEInventoryHandler cell = provider.provideCell();
		if (cell != null) {
			return cell.storedItemCount();
		}
		return 0;
	}

	@LuaMethod(description = "Get the amount of stored item types", returnType = LuaType.NUMBER)
	public long getStoredItemTypes(IComputerAccess computer, ICellProvider provider) {
		IMEInventoryHandler cell = provider.provideCell();
		if (cell != null) {
			return cell.storedItemTypes();
		}
		return 0;
	}

	@LuaMethod(description = "Get the total bytes", returnType = LuaType.NUMBER)
	public long getTotalBytes(IComputerAccess computer, ICellProvider provider) {
		IMEInventoryHandler cell = provider.provideCell();
		if (cell != null) {
			return cell.totalBytes();
		}
		return 0;
	}

	@LuaMethod(description = "Get the unused item count", returnType = LuaType.NUMBER)
	public long getUnusedItemCount(IComputerAccess computer, ICellProvider provider) {
		IMEInventoryHandler cell = provider.provideCell();
		if (cell != null) {
			return cell.unusedItemCount();
		}
		return 0;
	}

	@LuaMethod(description = "Get the unused bytes", returnType = LuaType.NUMBER)
	public long getUnusedBytes(IComputerAccess computer, ICellProvider provider) {
		IMEInventoryHandler cell = provider.provideCell();
		if (cell != null) {
			return cell.usedBytes();
		}
		return 0;
	}
	
}
