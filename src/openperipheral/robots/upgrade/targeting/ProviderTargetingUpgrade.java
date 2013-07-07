package openperipheral.robots.upgrade.targeting;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;
import openperipheral.core.item.ItemGeneric.Metas;

public class ProviderTargetingUpgrade implements IRobotUpgradeProvider {

	private HashMap<Integer, ItemStack> upgrades;
	
	public ProviderTargetingUpgrade() {
		
		upgrades = new HashMap<Integer, ItemStack>();
		
		upgrades.put(1, Metas.tier1targeting.newItemStack());
		upgrades.put(2, Metas.tier2targeting.newItemStack());
		upgrades.put(3, Metas.tier3targeting.newItemStack());
		
	}
	
	@Override
	public IRobotUpgradeInstance provideUpgradeInstance(IRobot robot, int tier) {
		return new InstanceTargetingUpgrade(robot, tier);
	}

	@Override
	public String getUpgradeId() {
		return "targeting";
	}

	@Override
	public Map<Integer, ItemStack> getUpgradeItems() {
		return upgrades;
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
		return InstanceTargetingUpgrade.class;
	}

}
