package openperipheral.util;

import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import openperipheral.IntegrationModuleRegistry;

import com.google.common.collect.Maps;

public class InventoryDescriptionUtils {

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

	public static Map<Integer, Map<String, Object>> invToMap(IInventory inventory) {
		Map<Integer, Map<String, Object>> map = Maps.newHashMap();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			map.put((i + 1), itemstackToMap(inventory.getStackInSlot(i)));
		}
		return map;
	}

	public static Map<String, Object> itemstackToMap(ItemStack itemstack) {
		Map<String, Object> map = Maps.newHashMap();

		if (itemstack == null) {

			map.put("id", 0);
			map.put("name", "empty");
			map.put("rawName", "empty");
			map.put("qty", 0);
			map.put("dmg", 0);
			map.put("maxdmg", 0);
			map.put("maxSize", 64);

		} else {
			map.put("id", itemstack.itemID);
			map.put("name", getNameForItemStack(itemstack));
			map.put("rawName", getRawNameForStack(itemstack));
			map.put("qty", itemstack.stackSize);
			map.put("dmg", itemstack.getItemDamage());
			map.put("maxdmg", itemstack.getMaxDamage());
			map.put("maxSize", itemstack.getMaxStackSize());

			IntegrationModuleRegistry.appendItemInfo(map, itemstack);
		}

		return map;
	}
}
