package openperipheral.common.util;

import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import openperipheral.Mods;
import openperipheral.OpenPeripheral;

public class MiscUtils {
	public static boolean canBeGlasses(ItemStack stack) {
		return stack != null && (stack.getItem() == OpenPeripheral.Items.glasses ||
				(ModLoader.isModLoaded(Mods.MPS) && MPSUtils.isValidHelmet(stack)));
	}
	
}
