package openperipheral.robots.upgrade.sensor;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import openperipheral.OpenPeripheral;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;

public class ProviderSensorUpgrade implements IRobotUpgradeProvider {

	private HashMap<Integer, ItemStack> upgrades;
	
	public ProviderSensorUpgrade() {
		upgrades = new HashMap<Integer, ItemStack>();
		upgrades.put(1, new ItemStack(OpenPeripheral.Blocks.sensor));
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
		return upgrades;
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
