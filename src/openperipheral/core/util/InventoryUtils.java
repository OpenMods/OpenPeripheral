package openperipheral.core.util;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.common.Loader;

import openperipheral.core.Mods;
import openperipheral.core.integration.ModuleForestry;
import openperipheral.core.integration.ModuleMystcraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class InventoryUtils {

	public static final String FACTORIZATION_BARREL_CLASS = "factorization.common.TileEntityBarrel";

	public static String getNameForItemStack(ItemStack is) {
		String name = "Unknown";
		try {
			name = is.getDisplayName();
		} catch (Exception e) {
			try {
				name = is.getUnlocalizedName();
			} catch (Exception e2) {}
		}
		return name;
	}

	public static String getRawNameForStack(ItemStack is) {

		String rawName = "unknown";

		try {
			rawName = is.getUnlocalizedName().toLowerCase();
		} catch (Exception e) {}
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

	public static void insertItemIntoInventory(IInventory inventory, ItemStack stack, ForgeDirection side) {
		int i = 0;
		while (stack.stackSize > 0 && i < inventory.getSizeInventory()) {
			if (side != ForgeDirection.UNKNOWN && inventory instanceof ISidedInventory) {
				if (!((ISidedInventory)inventory).canInsertItem(i, stack, side.ordinal())) {
					i++;
					continue;
				}
			}
			tryMergeStacks(inventory, i, stack);
			i++;
		}
	}

	public static Map invToMap(IInventory inventory) {
		HashMap map = new HashMap();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			map.put((i + 1), itemstackToMap(inventory.getStackInSlot(i)));
		}

		if (inventory.getClass().getName() == FACTORIZATION_BARREL_CLASS) {
			try {
				TileEntity barrel = (TileEntity)inventory;
				NBTTagCompound compound = new NBTTagCompound();
				barrel.writeToNBT(compound);
				HashMap firstStack = (HashMap)map.get(1);
				firstStack.put("size", compound.getInteger("item_count"));
				firstStack.put("maxStack", compound.getInteger("upgrade") == 1? 65536 : 4096);
			} catch (Exception e) {}
		}
		return map;
	}

	public static Map itemstackToMap(ItemStack itemstack) {

		HashMap map = new HashMap();

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

			if (Loader.isModLoaded(Mods.MYSTCRAFT)) {
				ModuleMystcraft.appendMystcraftInfo(map, itemstack);
			}
			if (Loader.isModLoaded(Mods.FORESTRY)) {
				ModuleForestry.appendBeeInfo(map, itemstack);
			}

		}

		return map;
	}

	protected static HashMap getBookEnchantments(ItemStack stack) {
		HashMap response = new HashMap();
		ItemEnchantedBook book = (ItemEnchantedBook)stack.getItem();
		NBTTagList nbttaglist = book.func_92110_g(stack);
		int offset = 1;
		if (nbttaglist != null) {
			for (int i = 0; i < nbttaglist.tagCount(); ++i) {
				short short1 = ((NBTTagCompound)nbttaglist.tagAt(i)).getShort("id");
				short short2 = ((NBTTagCompound)nbttaglist.tagAt(i)).getShort("lvl");

				if (Enchantment.enchantmentsList[short1] != null) {
					response.put(offset, Enchantment.enchantmentsList[short1].getTranslatedName(short2));
					offset++;
				}
			}
		}
		return response;
	}

	public static int moveItemInto(IInventory fromInventory, int slot, IInventory targetInventory, int intoSlot, int maxAmount, ForgeDirection direction) {
		if (!InventoryUtils.canMoveItem(fromInventory, targetInventory, slot, intoSlot, direction)) {
			return 0;
		}
		int merged = 0;
		ItemStack stack = fromInventory.getStackInSlot(slot);
		if (stack == null) { return merged; }
		ItemStack clonedStack = stack.copy();
		clonedStack.stackSize = Math.min(clonedStack.stackSize, maxAmount);
		int amountToMerge = clonedStack.stackSize;
		InventoryUtils.tryMergeStacks(targetInventory, intoSlot, clonedStack);
		merged = (amountToMerge - clonedStack.stackSize);
		fromInventory.decrStackSize(slot, merged);
		return merged;
	}

	public static int moveItem(IInventory fromInventory, int slot, IInventory targetInventory, int maxAmount, ForgeDirection side) {
		int merged = 0;
		ItemStack stack = fromInventory.getStackInSlot(slot);
		if (stack == null) { return 0; }
		if (fromInventory instanceof ISidedInventory) {
			if (!((ISidedInventory)fromInventory).canExtractItem(slot, stack, side.ordinal())) {
				return 0;
			}
		}
		ItemStack clonedStack = stack.copy();
		clonedStack.stackSize = Math.min(clonedStack.stackSize, maxAmount);
		int amountToMerge = clonedStack.stackSize;
		InventoryUtils.insertItemIntoInventory(targetInventory, clonedStack, side.getOpposite());
		merged = (amountToMerge - clonedStack.stackSize);
		fromInventory.decrStackSize(slot, merged);
		return merged;
	}

	public static IInventory getInventory(World world, int x, int y, int z, ForgeDirection direction) {
		/* If a direction is supplied, shift the coordinate paramters by their offset */
	    if (direction != null && direction != ForgeDirection.UNKNOWN) {
			x += direction.offsetX;
			y += direction.offsetY;
			z += direction.offsetZ;
		}
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		return getLargeInventory(tileEntity);
	}
	
	/**
	 * Returns the inventory of a TileEntity
	 * @param te The TileEntity to get the inventory for
	 * @return IInventory of any generic TE Inventory or an InventoryLargeChest for Double Chests.
	 */
	private static IInventory getLargeInventory(TileEntity te) {
	    if(te != null && te instanceof IInventory) {
	        if(te instanceof TileEntityChest) {
	            TileEntityChest chest = (TileEntityChest)te;
	            if(!chest.adjacentChestChecked) {
	                try {
	                    chest.checkForAdjacentChests();
	                }catch(Exception ex) {
	                    ex.printStackTrace();
	                    return (IInventory)te;
	                }
	            }
                /* Lol, Incorrect name. That's positive not position. I'll report that to MCP -NC */
                if(chest.adjacentChestZPosition != null) return new InventoryLargeChest("container.chestDouble", chest, chest.adjacentChestZPosition);
                if(chest.adjacentChestZNeg != null) return new InventoryLargeChest("container.chestDouble", chest.adjacentChestZNeg, chest);
                if(chest.adjacentChestXPos != null) return new InventoryLargeChest("container.chestDouble", chest, chest.adjacentChestXPos);
	            if(chest.adjacentChestXNeg != null) return new InventoryLargeChest("container.chestDouble", chest.adjacentChestXNeg, chest);
	            return chest;	            
	        } 
	        return (IInventory)te;
	    }
	    return null;
	}

	public static boolean canMoveItem(Object fromTile, Object toTile, int fromSlot, int intoSlot, ForgeDirection direction) {
		ItemStack stack = ((IInventory)fromTile).getStackInSlot(fromSlot);
		if (stack != null && fromTile instanceof ISidedInventory) {
			if (!((ISidedInventory)fromTile).canExtractItem(fromSlot, stack, direction.ordinal())) {
				return false;
			}
		}
		if (stack != null && toTile instanceof ISidedInventory) {
			if (!((ISidedInventory)toTile).canInsertItem(intoSlot, stack, direction.getOpposite().ordinal())) {
				return false;
			}
		}
		return true;
	}
}
