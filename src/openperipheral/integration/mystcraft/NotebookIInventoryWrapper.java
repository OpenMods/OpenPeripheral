package openperipheral.integration.mystcraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import openmods.utils.ReflectionHelper;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Preconditions;

public class NotebookIInventoryWrapper implements IInventory {

	private static final Class<?> INVENTORY_CLASS = ReflectionHelper.getClass("com.xcompwiz.mystcraft.inventory.InventoryNotebook");

	private ItemStack notebook;

	public NotebookIInventoryWrapper(ItemStack notebook) {
		Preconditions.checkNotNull(notebook, "No notebook");
		this.notebook = notebook;
	}

	public <T> T callOnNotebook(String method, Object... extras) {
		Object args[] = new Object[] { notebook };
		if (extras.length > 0) args = ArrayUtils.addAll(args, extras);
		return ReflectionHelper.callStatic(INVENTORY_CLASS, method, args);
	}

	@Override
	public int getSizeInventory() {
		Integer slotId = callOnNotebook("getLargestSlotId");
		return slotId + 2;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return callOnNotebook("getItem", ReflectionHelper.primitive(i));
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if (stack == null) return null;
		if (stack.stackSize < amount) amount = stack.stackSize;

		ItemStack returning = stack.copy();
		returning.stackSize = amount;

		stack.stackSize -= amount;
		if (stack.stackSize <= 0) setInventorySlotContents(slot, stack);

		return returning;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack pageStack) {
		callOnNotebook("setItem", ReflectionHelper.primitive(slot), ReflectionHelper.typed(pageStack, ItemStack.class));
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public String getInvName() {
		return callOnNotebook("getName");
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return Integer.MAX_VALUE; // I don't believe there is any limit to the
									// storage of a notebook
	}

	@Override
	public void onInventoryChanged() {}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return ReflectionHelper.callStatic(INVENTORY_CLASS, "isItemValid", itemstack);
	}

}
