package openperipheral.core.util;

import java.util.HashMap;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class InventoryUtils {
	
	public static final String FACTORIZATION_BARREL_CLASS = "factorization.common.TileEntityBarrel";
	
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
		if (targetInventory.isItemValidForSlot(slot, stack)) {
			ItemStack targetStack = targetInventory.getStackInSlot(slot);
			if (targetStack == null) {
				targetInventory.setInventorySlotContents(slot, stack.copy());
				stack.stackSize = 0;
			} else {
				boolean valid = targetInventory.isItemValidForSlot(slot, stack);
				if (valid && stack.itemID == targetStack.itemID && (!stack.getHasSubtypes() || stack.getItemDamage() == targetStack.getItemDamage())
						&& ItemStack.areItemStackTagsEqual(stack, targetStack) && targetStack.stackSize < targetStack.getMaxStackSize()) {
					int space = targetStack.getMaxStackSize() - targetStack.stackSize;
					int mergeAmount = Math.min(space, stack.stackSize);
					ItemStack copy = targetStack.copy();
					copy.stackSize += mergeAmount;
					targetInventory.setInventorySlotContents(slot, copy);
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
	
	public static void invToMap(HashMap map, IInventory inventory) {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			HashMap slotMap = (HashMap) map.get((i + 1));
			if (slotMap == null) {
				slotMap = new HashMap();
				map.put((i + 1), slotMap);
			}
			itemstackToMap(slotMap, inventory.getStackInSlot(i));
		}
		
		if (inventory.getClass().getName() == FACTORIZATION_BARREL_CLASS) {
			try {
				TileEntity barrel = (TileEntity) inventory;
				NBTTagCompound compound = new NBTTagCompound();
				barrel.writeToNBT(compound);
				HashMap firstStack = (HashMap) map.get(1);
				firstStack.put("size", compound.getInteger("item_count"));
				firstStack.put("maxStack", compound.getInteger("upgrade") == 1 ? 65536 : 4096);
			} catch (Exception e) {
			}
		}
	}
	
	public static void itemstackToMap(HashMap map, ItemStack itemstack) {

		if (itemstack == null) {

			map.put("id", 0);
			map.put("name", "empty");
			map.put("rawName", "empty");
			map.put("qty", 0);
			map.put("dmg", 0);
			map.put("maxSize", 64);

		} else {
			map.put("id", itemstack.itemID);
			map.put("name", getNameForItemStack(itemstack));
			map.put("rawName", getRawNameForStack(itemstack));
			map.put("qty", itemstack.stackSize);
			map.put("dmg", itemstack.getItemDamage());
			map.put("maxSize", itemstack.getMaxStackSize());

		}
	}

	protected static HashMap getBookEnchantments(ItemStack stack) {
		HashMap response = new HashMap();
		ItemEnchantedBook book = (ItemEnchantedBook) stack.getItem();
		NBTTagList nbttaglist = book.func_92110_g(stack);
		int offset = 1;
		if (nbttaglist != null) {
			for (int i = 0; i < nbttaglist.tagCount(); ++i) {
				short short1 = ((NBTTagCompound) nbttaglist.tagAt(i)).getShort("id");
				short short2 = ((NBTTagCompound) nbttaglist.tagAt(i)).getShort("lvl");

				if (Enchantment.enchantmentsList[short1] != null) {
					response.put(offset, Enchantment.enchantmentsList[short1].getTranslatedName(short2));
					offset++;
				}
			}
		}
		return response;
	}
	
	public static int moveItemInto(IInventory fromInventory, int slot, IInventory targetInventory, int intoSlot, int maxAmount) {
		int merged = 0;
		ItemStack stack = fromInventory.getStackInSlot(slot);
		if (stack == null) {
			return merged;
		}
		ItemStack clonedStack = stack.copy();
		clonedStack.stackSize = Math.min(clonedStack.stackSize, maxAmount);
		int amountToMerge = clonedStack.stackSize;
		InventoryUtils.tryMergeStacks(targetInventory, intoSlot, clonedStack);
		merged = (amountToMerge - clonedStack.stackSize);
		fromInventory.decrStackSize(slot, merged);
		return merged;
	}
	
	public static int moveItem(IInventory fromInventory, int slot, IInventory targetInventory, int maxAmount) {
		int merged = 0;
		ItemStack stack = fromInventory.getStackInSlot(slot);
		if (stack == null) {
			return 0;
		}
		ItemStack clonedStack = stack.copy();
		clonedStack.stackSize = Math.min(clonedStack.stackSize, maxAmount);
		int amountToMerge = clonedStack.stackSize;
		InventoryUtils.insertItemIntoInventory(targetInventory, clonedStack);
		merged = (amountToMerge - clonedStack.stackSize);
		fromInventory.decrStackSize(slot, merged);
		return merged;
	}
}
