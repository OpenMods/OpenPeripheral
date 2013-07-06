package openperipheral.robots.upgrade.targeting;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;

public class ProviderTargetingUpgrade implements IRobotUpgradeProvider {

	private HashMap<Integer, ItemStack> upgrades;
	
	public ProviderTargetingUpgrade() {
		
		upgrades = new HashMap<Integer, ItemStack>();
		
		//TOOD: change to something better
		upgrades.put(1, new ItemStack(Item.appleRed));
		
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
