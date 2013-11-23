package openperipheral.core.util;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.common.Loader;

import openmods.Mods;
import openperipheral.core.integration.ModuleForestry;
import openperipheral.core.integration.ModuleIC2;
import openperipheral.core.integration.ModuleMystcraft;
import openperipheral.core.integration.ModuleThermalExpansion;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class InventoryDescriptionUtils {

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

	@SuppressWarnings("unchecked")
	public static Map<Object, Object> invToMap(IInventory inventory) {
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			map.put((i + 1), itemstackToMap(inventory.getStackInSlot(i)));
		}

		if (inventory.getClass().getName() == FACTORIZATION_BARREL_CLASS) {
			try {
				TileEntity barrel = (TileEntity)inventory;
				NBTTagCompound compound = new NBTTagCompound();
				barrel.writeToNBT(compound);
				HashMap<Object, Object> firstStack = (HashMap<Object, Object>)map.get(1);
				firstStack.put("size", compound.getInteger("item_count"));
				firstStack.put("maxStack", compound.getInteger("upgrade") == 1? 65536 : 4096);
			} catch (Exception e) {}
		}
		return map;
	}

	public static Map<Object, Object> itemstackToMap(ItemStack itemstack) {

		HashMap<Object, Object> map = new HashMap<Object, Object>();

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
			if (Loader.isModLoaded(Mods.IC2)) {
				ModuleIC2.appendIC2Info(map, itemstack);
			}
			if (Loader.isModLoaded(Mods.THERMALEXPANSION)) {
				ModuleThermalExpansion.appendTEInfo(map, itemstack);
			}

		}

		return map;
	}

	protected static HashMap<Object, Object> getBookEnchantments(ItemStack stack) {
		HashMap<Object, Object> response = new HashMap<Object, Object>();
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

}
