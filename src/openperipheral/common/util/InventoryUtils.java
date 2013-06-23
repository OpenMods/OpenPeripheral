package openperipheral.common.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public class InventoryUtils {
	public static String getNameForItemStack(ItemStack is) {
		String name = "Unknown";
		try {
			name = is.getDisplayName();
		} catch (Exception e) {
			try {
				name = is.getItemName();
			} catch (Exception e2) {
			}
		}
		return name;
	}

	public static String getRawNameForStack(ItemStack is) {

		String rawName = "unknown";

		try {
			rawName = is.getItemName().toLowerCase();
		} catch (Exception e) {
		}
		try {
			if (rawName.length() - rawName.replaceAll("\\.", "").length() == 0) {
				String packageName = is.getItem().getClass().getName().toLowerCase();
				String[] packageLevels = packageName.split("\\.");
				if (!rawName.startsWith(packageLevels[0]) && packageLevels.length > 1) {
					rawName = packageLevels[0] + "." + rawName;
				}
			}
		} catch (Exception e) {

		}

		return rawName.trim();
	}

	public static void tryMergeStacks(IInventory targetInventory, int slot, ItemStack stack) {
		if (targetInventory.isStackValidForSlot(slot, stack)) {	
			ItemStack targetStack = targetInventory.getStackInSlot(slot);
			if (targetStack == null) {
				targetInventory.setInventorySlotContents(slot, stack.copy());
				stack.stackSize = 0;
			} else {
				boolean valid = targetInventory.isStackValidForSlot(slot, stack);
				if (valid && stack.itemID == targetStack.itemID &&
				  (!stack.getHasSubtypes() || stack.getItemDamage() == targetStack.getItemDamage()) &&
				  ItemStack.areItemStackTagsEqual(stack, targetStack) && targetStack.stackSize < targetStack.getMaxStackSize()) {
					int space = targetStack.getMaxStackSize() - targetStack.stackSize;
					int mergeAmount = Math.min(space, stack.stackSize); 
					ItemStack copy = targetStack.copy();
					copy.stackSize += mergeAmount;
					targetInventory.setInventorySlotContents(slot,  copy);
					stack.stackSize -= mergeAmount;
				}
			}
		}
	}

	public static void insertItemIntoInventory(IInventory inventory, ItemStack stack) {
		int i = 0;
		while (stack.stackSize > 0 && i < inventory.getSizeInventory()) {
			tryMergeStacks(inventory, i, stack);
			i++;
		}
	}
}
