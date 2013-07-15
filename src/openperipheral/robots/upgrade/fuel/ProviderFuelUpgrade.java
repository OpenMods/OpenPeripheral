package openperipheral.robots.upgrade.fuel;

import java.util.Map;

import net.minecraft.item.ItemStack;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeAdapter;
import openperipheral.api.IRobotUpgradeProvider;

public class ProviderFuelUpgrade implements IRobotUpgradeProvider {

	public ProviderFuelUpgrade() {
	}
	
	@Override
	public IRobotUpgradeAdapter provideUpgradeInstance(IRobot robot, int tier) {
		return new AdapterFuelUpgrade(robot);
	}

	@Override
	public String getUpgradeId() {
		return "fuel";
	}

	@Override
	public boolean isForced() {
		return true;
	}

	@Override
	public Map<Integer, ItemStack> getUpgradeItems() {
		return null;
	}

	@Override
	public boolean isApplicableForRobot(IRobot robot) {
		return true;
	}

	@Override
	public Class getUpgradeClass() {
		return AdapterFuelUpgrade.class;
	}

}
