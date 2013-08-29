package openperipheral.robots.upgrade.laser;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import openperipheral.api.EnumRobotType;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeAdapter;
import openperipheral.api.IRobotUpgradeProvider;
import openperipheral.core.item.ItemGeneric.Metas;

public class ProviderLasersUpgrade implements IRobotUpgradeProvider {

	HashMap<Integer, ItemStack> upgradeItems;

	public ProviderLasersUpgrade() {
		upgradeItems = new HashMap<Integer, ItemStack>();
		upgradeItems.put(1, Metas.tier1laser.newItemStack());
		upgradeItems.put(2, Metas.tier2laser.newItemStack());
		upgradeItems.put(3, Metas.tier3laser.newItemStack());
	}

	@Override
	public IRobotUpgradeAdapter provideUpgradeInstance(IRobot robot, int tier) {
		return new AdapterLasersUpgrade(robot, tier);
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
		return robot.getRobotType() == EnumRobotType.Warrior;
	}

	@Override
	public Class getUpgradeClass() {
		return AdapterLasersUpgrade.class;
	}

}
