package openperipheral.common.robotupgrades.targeting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;

public class ProviderTargetingUpgrade implements IRobotUpgradeProvider {

	private ArrayList<IRobotMethod> methods;
	private HashMap<Integer, ItemStack> upgrades;
	
	public ProviderTargetingUpgrade() {
		methods = new ArrayList<IRobotMethod>();
		methods.add(new MethodLookAt());
		methods.add(new MethodAimAt());
		
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
	public List<IRobotMethod> getMethods() {
		return methods;
	}

	@Override
	public boolean isApplicableForRobot(IRobot robot) {
		return true;
	}

}
