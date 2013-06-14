package openperipheral.common.util;

import net.minecraft.item.ItemStack;

public class InventoryUtils {
	public static int[] mapColors = new int[] {
		32768, 	// black
		32, 	// lime
		16, 	// yellow
		256, 	// light gray
		16384, 	// red
		2048, 	// blue
		128, 	// gray
		8192, 	// green
		1, 		// white
		512, 	// cyan
		4096, 	// brown
		128, 	// gray
		2048, 	// blue
		4096 	// brown	
	};
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
				String packageName = is.getItem().getClass().getName()
						.toLowerCase();
				String[] packageLevels = packageName.split("\\.");
				if (!rawName.startsWith(packageLevels[0])
						&& packageLevels.length > 1) {
					rawName = packageLevels[0] + "." + rawName;
				}
			}
		} catch (Exception e) {

		}

		return rawName.trim();
	}
}
