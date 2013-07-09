package openperipheral.robots.upgrade.inventory;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;
import openperipheral.core.item.ItemGeneric.Metas;

public class ProviderInventoryUpgrade implements IRobotUpgradeProvider {
	
	private HashMap<Integer, ItemStack> upgradeItems;
	
	public ProviderInventoryUpgrade() {
		
		upgradeItems = new HashMap<Integer, ItemStack>();
		upgradeItems.put(1, Metas.tier1inventory.newItemStack());
		upgradeItems.put(2, Metas.tier2inventory.newItemStack());
	}

	@Override
	public IRobotUpgradeInstance provideUpgradeInstance(IRobot robot, int tier) {
		return new InstanceInventoryUpgrade(robot, tier);
	}

	@Override
	public String getUpgradeId() {
		return "inventory";
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
		return InstanceInventoryUpgrade.class;
	}

}
