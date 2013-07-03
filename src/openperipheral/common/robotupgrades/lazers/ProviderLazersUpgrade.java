package openperipheral.common.robotupgrades.lazers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;

public class ProviderLazersUpgrade implements IRobotUpgradeProvider {

	ArrayList<IRobotMethod> methods;
	
	public ProviderLazersUpgrade() {
		methods = new ArrayList<IRobotMethod>();
		methods.add(new MethodLazerFire());
	}
	
	@Override
	public IRobotUpgradeInstance provideUpgradeInstance(IRobot robot, int tier) {
		return new InstanceLazersUpgrade(robot);
	}

	@Override
	public String getUpgradeId() {
		return "lazers";
	}

	@Override
	public List<IRobotMethod> getMethods() {
		return methods;
	}

	@Override
	public boolean isForced() {
		return false;
	}

	@Override
	public Map<Integer, ItemStack> getUpgradeItems() {
		// TODO Auto-generated method stub
		return null;
	}

}
