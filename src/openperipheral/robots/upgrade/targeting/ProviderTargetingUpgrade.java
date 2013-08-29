package openperipheral.robots.upgrade.targeting;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeAdapter;
import openperipheral.api.IRobotUpgradeProvider;
import openperipheral.core.item.ItemGeneric.Metas;

public class ProviderTargetingUpgrade implements IRobotUpgradeProvider {

	private HashMap<Integer, ItemStack> upgradeItems;

	public ProviderTargetingUpgrade() {

		upgradeItems = new HashMap<Integer, ItemStack>();

		upgradeItems.put(1, Metas.tier1targeting.newItemStack());
		upgradeItems.put(2, Metas.tier2targeting.newItemStack());
		upgradeItems.put(3, Metas.tier3targeting.newItemStack());

	}

	@Override
	public IRobotUpgradeAdapter provideUpgradeInstance(IRobot robot, int tier) {
		return new AdapterTargetingUpgrade(robot, tier);
	}

	@Override
	public String getUpgradeId() {
		return "targeting";
	}

	@Override
	public Map<Integer, ItemStack> getUpgradeItems() {
		return upgradeItems;
	}

	@Override
	public boolean isForced() {
		return false;
	}

	@Override
	public boolean isApplicableForRobot(IRobot robot) {
		return true;
	}

	@Override
	public Class getUpgradeClass() {
		return AdapterTargetingUpgrade.class;
	}

}
