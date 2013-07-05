package openperipheral.robots.common.upgrade.fuel;

import java.util.Map;

import net.minecraft.item.ItemStack;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;

public class ProviderFuelUpgrade implements IRobotUpgradeProvider {

	public ProviderFuelUpgrade() {
	}
	
	@Override
	public IRobotUpgradeInstance provideUpgradeInstance(IRobot robot, int tier) {
		return new InstanceFuelUpgrade(robot);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isApplicableForRobot(IRobot robot) {
		return true;
	}

	@Override
	public Class getUpgradeClass() {
		return InstanceFuelUpgrade.class;
	}

}
