package openperipheral.common.util;

import net.machinemuse.api.ModuleManager;
import net.machinemuse.utils.MuseItemUtils;
import net.minecraft.item.ItemStack;
import openperipheral.common.integration.mps.GlassesModule;

public class MPSUtils {
	public static boolean isValidHelmet(ItemStack stack) {
		return MuseItemUtils.itemHasActiveModule(stack, "OpenPeripheral Terminal Module");
	}

	public static void initModule() {
		ModuleManager.addModule(new GlassesModule());
	}
}
