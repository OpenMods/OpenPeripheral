package openperipheral.common.util;

import java.util.Calendar;

import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import openperipheral.OpenPeripheral;
import openperipheral.common.core.Mods;

public class MiscUtils {
	public static boolean canBeGlasses(ItemStack stack) {
		return stack != null && (stack.getItem() == OpenPeripheral.Items.glasses || (ModLoader.isModLoaded(Mods.MPS) && MPSUtils.isValidHelmet(stack)));
	}

	public static int getHoliday() {
		Calendar today = Calendar.getInstance();
		int month = today.get(2);
		int day = today.get(5);
		if ((month == 1) && (day == 14)) {
			return 1;
		}
		if ((month == 9) && (day == 31)) {
			return 2;
		}
		if ((month == 11) && (day >= 24) && (day <= 30)) {
			return 3;
		}
		return 0;
	}
}
