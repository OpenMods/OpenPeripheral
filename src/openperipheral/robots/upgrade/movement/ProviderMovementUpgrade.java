package openperipheral.robots.upgrade.movement;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;
import openperipheral.core.item.ItemGeneric.Metas;

public class ProviderMovementUpgrade implements IRobotUpgradeProvider {
	
	private HashMap<Integer, ItemStack> upgradeItems = new HashMap<Integer, ItemStack>();
	
	public ProviderMovementUpgrade() {
		upgradeItems.put(1, Metas.tier1movement.newItemStack());
		upgradeItems.put(2, Metas.tier2movement.newItemStack());
		upgradeItems.put(3, Metas.tier3movement.newItemStack());
	}
	
	@Override
	public IRobotUpgradeInstance provideUpgradeInstance(IRobot robot, int tier) {
		return new InstanceMovementUpgrade(robot, tier);
	}

	@Override
	public String getUpgradeId() {
		return "movement";
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
		return InstanceMovementUpgrade.class;
	}

}
