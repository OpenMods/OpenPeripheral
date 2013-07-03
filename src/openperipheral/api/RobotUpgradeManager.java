package openperipheral.api;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

public class RobotUpgradeManager {
	
	private static List<IRobotUpgradeProvider> providers = new ArrayList<IRobotUpgradeProvider>();
	
	public static List<IRobotUpgradeProvider> getProviders() {
		return providers;
	}
	
	public static void registerUpgradeProvider(IRobotUpgradeProvider upgrade) {
		providers.add(upgrade);
	}
	
	public static IRobotUpgradeProvider getProviderById(String id) {
		for (IRobotUpgradeProvider provider : providers) {
			if (provider.getUpgradeId().equals(id)) {
				return provider;
			}
		}
		return null;
	}
	
	public static IRobotUpgradeProvider getProviderForStack(ItemStack stack) {
		if (stack == null) {
			return null;
		}
		for (IRobotUpgradeProvider provider : providers) {
			ItemStack upgradeStack = provider.getUpgradeItem();
			if (upgradeStack == null) {
				continue;
			}
			if (upgradeStack.itemID == stack.itemID && upgradeStack.getItemDamage() == stack.getItemDamage()) {
				return provider;
			}
		}
		return null;
	}
	
}
