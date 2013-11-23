package openperipheral.core.adapter.appeng;

import java.util.Iterator;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import openmods.utils.InventoryUtils;
import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.core.util.CallWrapper;
import openperipheral.core.util.ReflectionHelper;
import appeng.api.IAEItemStack;
import appeng.api.IItemList;
import appeng.api.Util;
import appeng.api.exceptions.AppEngTileMissingException;
import appeng.api.me.util.IGridInterface;
import appeng.api.me.util.IMEInventoryHandler;
import dan200.computer.api.IComputerAccess;

public class AdapterGridTileEntity implements IPeripheralAdapter {
	private static final Class<?> CLAZZ = ReflectionHelper.getClass("appeng.api.me.tiles.IGridTileEntity");

	@Override
	public Class<?> getTargetClass() {
		return CLAZZ;
	}

	private IGridInterface getGrid(Object tile) {
		return new CallWrapper<IGridInterface>().call(tile, "getGrid");
	}

	@LuaMethod(description = "Request crafting of a specific item", returnType = LuaType.VOID,
			args = {
			@Arg(type = LuaType.TABLE, name = "stack", description = "A table representing the item stack") })
	public void requestCrafting(IComputerAccess computer, Object te, ItemStack stack) throws AppEngTileMissingException {
		getGrid(te).craftingRequest(stack);
	}


	@LuaMethod(description = "Extract an item", returnType = LuaType.NUMBER,
			args = {
			@Arg(type = LuaType.TABLE, name = "stack", description = "A table representing the item stack"),
			@Arg(type = LuaType.STRING, name = "direction", description = "The direction of the chest relative to the wrapped peripheral")})
	public int extractItem(IComputerAccess computer, Object te, ItemStack stack, ForgeDirection direction) {
		if (stack == null) {
			return 0;
		}
		IGridInterface grid = getGrid(te);
		if (grid == null) {
			return 0;
		}
		IAEItemStack request = Util.createItemStack(stack);
		if (request == null) {
			return 0;
		}
		IAEItemStack returned = grid.getCellArray().extractItems(request);
		if (returned == null) {
			return 0;
		}
		IAEItemStack giveBack = null;
		int requestAmount = stack.stackSize;
		if (!(te instanceof TileEntity)) {
			return 0;
		}
		TileEntity tile = (TileEntity) te;
		IInventory inventory = InventoryUtils.getInventory(tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord, direction);
		if (inventory == null) {
			giveBack = returned.copy();
		}else {
			ItemStack returnedStack = returned.getItemStack();
			InventoryUtils.insertItemIntoInventory(inventory, returnedStack, direction.getOpposite(), -1);
			giveBack = Util.createItemStack(returnedStack.copy());
		}
		if (giveBack != null) {
			grid.getCellArray().addItems(giveBack);
		}
		if (giveBack != null) {
			return requestAmount - (int)giveBack.getStackSize();
		}
		return requestAmount;
	}

	@LuaMethod(description = "Insert an item back into the system", returnType = LuaType.NUMBER,
			args = {
			@Arg(type = LuaType.NUMBER, name = "slot", description = "The slot you wish to send"),
			@Arg(type = LuaType.NUMBER, name = "amount", description = "The amount you want to send"),
			@Arg(type = LuaType.STRING, name = "direction", description = "The direction of the chest relative to the wrapped peripheral")})
	public int insertItem(IComputerAccess computer, Object te, int slot, int amount, ForgeDirection direction) throws Exception {
		TileEntity tile = (TileEntity) te;
		IGridInterface grid = getGrid(te);
		if (grid == null) {
			return 0;
		}
		IInventory inventory = InventoryUtils.getInventory(tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord, direction);
		if (inventory == null) {
			return 0;
		}
		slot--;
		if (slot < 0 || slot >= inventory.getSizeInventory()) {
			throw new Exception("Slot is out of range");
		}
		ItemStack stack = inventory.getStackInSlot(slot);
		if (stack == null) {
			return 0;
		}
		amount = Math.min(amount, stack.stackSize);
		amount = Math.max(amount, 0);
		ItemStack sendStack = stack.copy();
		sendStack.stackSize = amount;
		IAEItemStack request = Util.createItemStack(sendStack);
		IAEItemStack remaining = grid.getCellArray().addItems(request);

		if (remaining == null) {
			stack.stackSize -= amount;
			if (stack.stackSize <= 0) {
				inventory.setInventorySlotContents(slot, null);
			}else {
				inventory.setInventorySlotContents(slot, stack);
			}
			return amount;
		}
		int sent = (int)(amount - remaining.getStackSize());
		if (sent <= 0) {
			inventory.setInventorySlotContents(slot, null);
		}else {
			stack.stackSize -= sent;
			inventory.setInventorySlotContents(slot, stack);
		}
		return sent;
	}


	@LuaMethod(description = "Get the total total item types stored", returnType = LuaType.NUMBER)
	public long getTotalItemTypes(IComputerAccess computer, Object te) {
		IGridInterface grid = getGrid(te);
		if (grid != null) {
			IMEInventoryHandler cell = grid.getCellArray();
			if (cell != null) {
				return cell.getTotalItemTypes();
			}
		}
		return 0;
	}

	@LuaMethod(description = "Get the priority of this machine", returnType = LuaType.NUMBER)
	public int getPriority(IComputerAccess computer, Object te) {
		IGridInterface grid = getGrid(te);
		if (grid != null) {
			IMEInventoryHandler cell = grid.getCellArray();
			if (cell != null) {
				return cell.getPriority();
			}
		}
		return 0;
	}

	@LuaMethod(description = "Can this machine hold any new items?", returnType = LuaType.NUMBER)
	public boolean canHoldNewItem(IComputerAccess computer, Object te) {
		IGridInterface grid = getGrid(te);
		if (grid != null) {
			IMEInventoryHandler cell = grid.getCellArray();
			if (cell != null) {
				return cell.canHoldNewItem();
			}
		}
		return false;
	}

	@LuaMethod(description = "Get the amount of free bytes", returnType = LuaType.NUMBER)
	public long getFreeBytes(IComputerAccess computer, Object te) {
		IGridInterface grid = getGrid(te);
		if (grid != null) {
			IMEInventoryHandler cell = grid.getCellArray();
			if (cell != null) {
				return cell.freeBytes();
			}
		}
		return 0;
	}

	@LuaMethod(description = "Get a list of the available items", returnType = LuaType.TABLE)
	public IItemList getAvailableItems(IComputerAccess computer, Object te) {
		IGridInterface grid = getGrid(te);
		if (grid != null) {
			IMEInventoryHandler cell = grid.getCellArray();
			if (cell != null) {
				return cell.getAvailableItems();
			}
		}
		return null;
	}

	@LuaMethod(
			description = "Check to see if the network contains an item type",
			returnType = LuaType.BOOLEAN,
			args = {
					@Arg(type = LuaType.NUMBER, name = "itemId", description = "The item id"),
					@Arg(type = LuaType.NUMBER, name = "dmgValue", description = "The item dmg value") })
	public boolean containsItemType(IComputerAccess computer, Object te, int itemId, int dmgValue) {
		return countOfItemType(computer, te, itemId, dmgValue) > 0;
	}

	@LuaMethod(description = "Count the amount of a certain item type", returnType = LuaType.NUMBER,
			args = {
			@Arg(type = LuaType.NUMBER, name = "itemId", description = "The item id"),
			@Arg(type = LuaType.NUMBER, name = "dmgValue", description = "The item dmg value") })
	public long countOfItemType(IComputerAccess computer, Object te, int itemId, int dmgValue) {
		IGridInterface grid = getGrid(te);
		if (grid == null) {
			return 0;
		}
		IMEInventoryHandler cell = grid.getCellArray();
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

	@LuaMethod(description = "Get a list of the preformatted items", returnType = LuaType.TABLE)
	public List<ItemStack> getPreformattedItems(IComputerAccess computer, Object te) {
		IGridInterface grid = getGrid(te);
		if (grid != null) {
			IMEInventoryHandler cell = grid.getCellArray();
			if (cell != null) {
				return cell.getPreformattedItems();
			}
		}
		return null;
	}

	@LuaMethod(description = "Is fuzzy preformatted", returnType = LuaType.BOOLEAN)
	public boolean isFuzzyPreformatted(IComputerAccess computer, Object te) {
		IGridInterface grid = getGrid(te);
		if (grid != null) {
			IMEInventoryHandler cell = grid.getCellArray();
			if (cell != null) {
				return cell.isFuzzyPreformatted();
			}
		}
		return false;
	}

	@LuaMethod(description = "Is preformatted", returnType = LuaType.BOOLEAN)
	public boolean isPreformatted(IComputerAccess computer, Object te) {
		IGridInterface grid = getGrid(te);
		if (grid != null) {
			IMEInventoryHandler cell = grid.getCellArray();
			if (cell != null) {
				return cell.isPreformatted();
			}
		}
		return false;
	}

	@LuaMethod(description = "Get the remaining item count", returnType = LuaType.NUMBER)
	public long getRemainingItemCount(IComputerAccess computer, Object te) {
		IGridInterface grid = getGrid(te);
		if (grid != null) {
			IMEInventoryHandler cell = grid.getCellArray();
			if (cell != null) {
				return cell.remainingItemCount();
			}
		}
		return 0;
	}

	@LuaMethod(description = "Get the remaining item type count", returnType = LuaType.NUMBER)
	public long getRemainingItemTypes(IComputerAccess computer, Object te) {
		IGridInterface grid = getGrid(te);
		if (grid != null) {
			IMEInventoryHandler cell = grid.getCellArray();
			if (cell != null) {
				return cell.remainingItemTypes();
			}
		}
		return 0;
	}

	@LuaMethod(description = "Get the amount of stored items", returnType = LuaType.NUMBER)
	public long getStoredItemCount(IComputerAccess computer, Object te) {
		IGridInterface grid = getGrid(te);
		if (grid != null) {
			IMEInventoryHandler cell = grid.getCellArray();
			if (cell != null) {
				return cell.storedItemCount();
			}
		}
		return 0;
	}

	@LuaMethod(description = "Get the amount of stored item types", returnType = LuaType.NUMBER)
	public long getStoredItemTypes(IComputerAccess computer, Object te) {
		IGridInterface grid = getGrid(te);
		if (grid != null) {
			IMEInventoryHandler cell = grid.getCellArray();
			if (cell != null) {
				return cell.storedItemTypes();
			}
		}
		return 0;
	}

	@LuaMethod(description = "Get the total bytes", returnType = LuaType.NUMBER)
	public long getTotalBytes(IComputerAccess computer, Object te) {
		IGridInterface grid = getGrid(te);
		if (grid != null) {
			IMEInventoryHandler cell = grid.getCellArray();
			if (cell != null) {
				return cell.totalBytes();
			}
		}
		return 0;
	}

	@LuaMethod(description = "Get the unused item count", returnType = LuaType.NUMBER)
	public long getUnusedItemCount(IComputerAccess computer, Object te) {
		IGridInterface grid = getGrid(te);
		if (grid != null) {
			IMEInventoryHandler cell = grid.getCellArray();
			if (cell != null) {
				return cell.unusedItemCount();
			}
		}
		return 0;
	}

	@LuaMethod(description = "Get the unused bytes", returnType = LuaType.NUMBER)
	public long getUnusedBytes(IComputerAccess computer, Object te) {
		IGridInterface grid = getGrid(te);
		if (grid != null) {
			IMEInventoryHandler cell = grid.getCellArray();
			if (cell != null) {
				return cell.usedBytes();
			}
		}
		return 0;
	}

}
