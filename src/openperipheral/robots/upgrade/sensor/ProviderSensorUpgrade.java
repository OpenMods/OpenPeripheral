package openperipheral.robots.upgrade.sensor;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeAdapter;
import openperipheral.api.IRobotUpgradeProvider;
import openperipheral.core.item.ItemGeneric.Metas;

public class ProviderSensorUpgrade implements IRobotUpgradeProvider {

	private HashMap<Integer, ItemStack> upgradeItems;

	public ProviderSensorUpgrade() {
		upgradeItems = new HashMap<Integer, ItemStack>();
		upgradeItems.put(1, Metas.tier1sensor.newItemStack());
		upgradeItems.put(2, Metas.tier2sensor.newItemStack());
		upgradeItems.put(3, Metas.tier3sensor.newItemStack());
	}

	@Override
	public IRobotUpgradeAdapter provideUpgradeInstance(IRobot robot, int tier) {
		return new AdapterSensorUpgrade(robot, tier);
	}

	@Override
	public String getUpgradeId() {
		return "sensors";
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
		return AdapterSensorUpgrade.class;
	}

}
