package openperipheral.robots.upgrade.lazer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openperipheral.api.EnumRobotType;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;
import openperipheral.core.item.ItemGeneric.Metas;

public class ProviderLazersUpgrade implements IRobotUpgradeProvider {

	HashMap<Integer, ItemStack> upgradeItems;
	
	public ProviderLazersUpgrade() {
		upgradeItems = new HashMap<Integer, ItemStack>();
		upgradeItems.put(1, Metas.tier1lazer.newItemStack());
		upgradeItems.put(2, Metas.tier2lazer.newItemStack());
		upgradeItems.put(3, Metas.tier3lazer.newItemStack());
	}
	
	@Override
	public IRobotUpgradeInstance provideUpgradeInstance(IRobot robot, int tier) {
		return new InstanceLazersUpgrade(robot, tier);
	}

	@Override
	public String getUpgradeId() {
		return "lazers";
	}

	@Override
	public boolean isForced() {
		return false;
	}

	@Override
	public Map<Integer, ItemStack> getUpgradeItems() {
		return upgradeItems;
	}

	@Override
	public boolean isApplicableForRobot(IRobot robot) {
		return true;
	}

	@Override
	public Class getUpgradeClass() {
		return InstanceLazersUpgrade.class;
	}

}
