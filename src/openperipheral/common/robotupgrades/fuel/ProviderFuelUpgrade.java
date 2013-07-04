package openperipheral.common.robotupgrades.fuel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import openperipheral.api.EnumRobotType;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;

public class ProviderFuelUpgrade implements IRobotUpgradeProvider {

	private ArrayList<IRobotMethod> methods;
	public ProviderFuelUpgrade() {
		methods = new ArrayList<IRobotMethod>();
		methods.add(new MethodGetFuelLevel());
		methods.add(new MethodRefuel());
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
	public List<IRobotMethod> getMethods() {
		return methods;
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

}
