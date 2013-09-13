package openperipheral.core.adapter.appeng;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.core.util.CallWrapper;
import openperipheral.core.util.InventoryUtils;
import appeng.api.IAEItemStack;
import appeng.api.Util;
import appeng.api.exceptions.AppEngTileMissingException;
import appeng.api.me.util.ICraftRequest;
import appeng.api.me.util.IGridInterface;
import dan200.computer.api.IComputerAccess;

public class AdapterGridTileEntity implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		 try {
		    return Class.forName("appeng.api.me.tiles.IGridTileEntity");
		 } catch (ClassNotFoundException e) {
		   return null;
		 }
	}
	
	private IGridInterface getGrid(Object tile) {
		return new CallWrapper<IGridInterface>().call(tile, "getGrid");
	}

	@LuaMethod(description = "Request crafting of a specific item", returnType = LuaType.VOID,
		args = {
			@Arg(type = LuaType.TABLE, name = "stack", description = "A table representing the item stack") })
	public void requestCrafting(IComputerAccess computer, Object te, ItemStack stack) throws AppEngTileMissingException {
		ICraftRequest request = getGrid(te).craftingRequest(stack);
	}
	
	@LuaMethod(description = "Extract an item", returnType = LuaType.NUMBER,
			args = {
				@Arg(type = LuaType.TABLE, name = "stack", description = "A table representing the item stack"),
				@Arg(type = LuaType.STRING, name = "direction", description = "The direction of the chest relative to the wrapped peripheral")})
	public int extractItem(IComputerAccess computer, Object te, ItemStack stack, ForgeDirection direction) {
		if (stack == null) {
			return 0;
		}
		IAEItemStack request = Util.createItemStack(stack);
		if (request == null) {
			return 0;
		}
        IAEItemStack returned = getGrid(te).getCellArray().extractItems(request);
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
            InventoryUtils.insertItemIntoInventory(inventory, returnedStack);
            giveBack = Util.createItemStack(returnedStack.copy());
        }
        if (giveBack != null) {
            getGrid(te).getCellArray().addItems(giveBack);
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
		IInventory inventory = InventoryUtils.getInventory(tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord, direction);
		if (inventory == null) {
			return 0;
		}
		slot--;
		if (slot < 0 || slot >= inventory.getSizeInventory()) {
			throw new Exception("Slot is out of range");
		}
		ItemStack stack = inventory.getStackInSlot(slot);
		amount = Math.min(amount, stack.stackSize);
		amount = Math.max(amount, 0);
		ItemStack sendStack = stack.copy();
		sendStack.stackSize = amount;
		IAEItemStack request = Util.createItemStack(sendStack);
		IAEItemStack remaining = getGrid(te).getCellArray().addItems(request);
		
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

}
