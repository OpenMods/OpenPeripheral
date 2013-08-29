package openperipheral.robots;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import openperipheral.api.IRobotUpgradeProvider;
import openperipheral.core.AdapterManager;

public class RobotUpgradeManager {

	private static List<IRobotUpgradeProvider> providers = new ArrayList<IRobotUpgradeProvider>();

	public static List<IRobotUpgradeProvider> getProviders() {
		return providers;
	}

	public static void registerUpgradeProvider(IRobotUpgradeProvider upgrade) {
		providers.add(upgrade);
		AdapterManager.addRobotAdapter(upgrade.getUpgradeClass());
	}

	public static IRobotUpgradeProvider getProviderById(String id) {
		for (IRobotUpgradeProvider provider : providers) {
			if (provider.getUpgradeId().equals(id)) { return provider; }
		}
		return null;
	}

	public static IRobotUpgradeProvider getProviderForStack(ItemStack stack) {
		if (stack == null) { return null; }
		for (IRobotUpgradeProvider provider : providers) {
			Map<Integer, ItemStack> upgradeStacks = provider.getUpgradeItems();
			if (upgradeStacks == null) {
				continue;
			}
			for (ItemStack upgradeStack : upgradeStacks.values()) {
				if (upgradeStack.itemID == stack.itemID && upgradeStack.getItemDamage() == stack.getItemDamage()) { return provider; }
			}
		}
		return null;
	}

	public static int getTierForUpgradeItem(IRobotUpgradeProvider provider, ItemStack stack) {
		Map<Integer, ItemStack> upgradeStacks = provider.getUpgradeItems();
		if (upgradeStacks != null) {
			for (Entry<Integer, ItemStack> entry : upgradeStacks.entrySet()) {
				ItemStack upgradeStack = entry.getValue();
				if (upgradeStack.itemID == stack.itemID && upgradeStack.getItemDamage() == stack.getItemDamage()) { return entry.getKey(); }
			}
		}
		return -1;
	}

}
