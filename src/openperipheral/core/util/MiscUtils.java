package openperipheral.core.util;

import java.util.Arrays;
import java.util.Calendar;

import net.minecraft.item.ItemStack;
import openperipheral.OpenPeripheral;

public class MiscUtils {
	public static boolean canBeGlasses(ItemStack stack) {
		// || (Loader.isModLoaded(Mods.MPS) && MPSUtils.isValidHelmet(stack)))
		return stack != null && (stack.getItem() == OpenPeripheral.Items.glasses);
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
	
	public static <T> T[] append(T[] arr, T element) {
	    final int N = arr.length;
	    arr = Arrays.copyOf(arr, N + 1);
	    arr[N] = element;
	    return arr;
	}
	
	public static String getNameForTarget(Object target) {
		return "test";
	}
}
