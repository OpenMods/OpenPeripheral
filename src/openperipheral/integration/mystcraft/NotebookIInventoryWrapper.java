package openperipheral.integration.mystcraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import openperipheral.util.CallWrapper;
import cpw.mods.fml.common.FMLLog;

public class NotebookIInventoryWrapper implements IInventory {

	private static final String QUALIFIED_NAME_INVENTORY_NOTEBOOK = "com.xcompwiz.mystcraft.inventory.InventoryNotebook";

	private Object notebook;
	public Class<?> inventoryNotebookClass;

	public NotebookIInventoryWrapper(ItemStack notebook) throws ClassNotFoundException {
		this.notebook = notebook;
		this.inventoryNotebookClass = Class.forName(QUALIFIED_NAME_INVENTORY_NOTEBOOK);
	}

	@Override
	public int getSizeInventory() {
		FMLLog.warning("NotebookIInventoryWrapper.getSizeInventory");
		return new CallWrapper<Integer>().call(inventoryNotebookClass, "getLargestSlotId", notebook) + 2;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		FMLLog.warning("NotebookIInventoryWrapper.getStackInSlot(%d)", i);
		return new CallWrapper<ItemStack>().call(inventoryNotebookClass, "getItem", notebook, i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		FMLLog.warning("NotebookIInventoryWrapper.decrStackSize(%d,%d)", i, j);
		ItemStack stack = getStackInSlot(i);
		if (stack != null) {
			ItemStack returning = stack.copy();
			stack = stack.copy();
			stack.stackSize -= Math.min(j, stack.stackSize);
			returning.stackSize = returning.stackSize - stack.stackSize;
			if (stack.stackSize <= 0) {
				stack = null;
			}
			setInventorySlotContents(i, stack);
			return returning;
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		FMLLog.warning("NotebookIInventoryWrapper.getStackInSlotOnClosing(%d)", i);
		ItemStack stack = getStackInSlot(i);
		setInventorySlotContents(i, null);
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		FMLLog.warning("NotebookIInventoryWrapper.setInventorySlotContents(%d,%s)", slot, itemStack == null? "null" : itemStack.toString());
		new CallWrapper<Void>().call(inventoryNotebookClass, "setItem", notebook, slot, itemStack);
	}

	@Override
	public String getInvName() {
		FMLLog.warning("NotebookIInventoryWrapper.getInvName()");
		return new CallWrapper<String>().call(inventoryNotebookClass, "getName", notebook);
	}

	@Override
	public boolean isInvNameLocalized() {
		FMLLog.warning("NotebookIInventoryWrapper.isInvNameLocalized()");
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		FMLLog.warning("NotebookIInventoryWrapper.getInventoryStackLimit()");
		return Integer.MAX_VALUE; // I don't believe there is any limit to the
									// storage of a notebook
	}

	@Override
	public void onInventoryChanged() {
		FMLLog.warning("NotebookIInventoryWrapper.onInventoryChanged()");

	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		FMLLog.warning("NotebookIInventoryWrapper.isUseableByPlayer(%s)", entityplayer.toString());
		return true;
	}

	@Override
	public void openChest() {
		FMLLog.warning("NotebookIInventoryWrapper.openChest()");

	}

	@Override
	public void closeChest() {
		FMLLog.warning("NotebookIInventoryWrapper.closeChest()");
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		FMLLog.warning("NotebookIInventoryWrapper.isItemValidForSlot(%d,%s)", i, itemstack.toString());
		return new CallWrapper<Boolean>().call(inventoryNotebookClass, "isItemValid", itemstack);
	}

}
