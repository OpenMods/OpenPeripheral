package openperipheral.common.robotupgrades.lazers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeDefinition;
import openperipheral.api.IRobotUpgradeInstance;

public class LazersUpgradeSupplier implements IRobotUpgradeDefinition {

	ArrayList<IRobotMethod> methods;
	
	public LazersUpgradeSupplier() {
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

}
