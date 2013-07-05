package openperipheral.robots.common.upgrade.sensor;

import java.util.Map;

import net.minecraft.item.ItemStack;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;

public class ProviderSensorUpgrade implements IRobotUpgradeProvider {

	
	public ProviderSensorUpgrade() {
	}
	
	@Override
	public IRobotUpgradeInstance provideUpgradeInstance(IRobot robot, int tier) {
		return new InstanceSensorUpgrade(robot);
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
		return null;
	}

	@Override
	public boolean isApplicableForRobot(IRobot robot) {
		return true;
	}

	@Override
	public Class getUpgradeClass() {
		return InstanceSensorUpgrade.class;
	}

}
