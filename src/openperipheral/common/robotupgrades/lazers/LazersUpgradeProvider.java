package openperipheral.common.robotupgrades.lazers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeProvider;
import openperipheral.api.IRobotUpgradeInstance;

public class LazersUpgradeProvider implements IRobotUpgradeProvider {

	ArrayList<IRobotMethod> methods;
	
	public LazersUpgradeProvider() {
		methods = new ArrayList<IRobotMethod>();
		methods.add(new MethodLazerFire());
	}
	
	@Override
	public IRobotUpgradeInstance provideUpgradeInstance(IRobot robot) {
		return new LazersUpgrade(robot);
	}

	@Override
	public String getUpgradeId() {
		return "lazers";
	}

	@Override
	public ItemStack getUpgradeItem() {
		return null;
	}

	@Override
	public List<IRobotMethod> getMethods() {
		return methods;
	}

	@Override
	public boolean isForced() {
		return false;
	}

}
