package openperipheral.common.robotupgrades.movement;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.IRobotUpgradeProvider;

public class ProviderMovementUpgrade implements IRobotUpgradeProvider {

	ArrayList<IRobotMethod> methods;
	
	public ProviderMovementUpgrade() {
		methods = new ArrayList<IRobotMethod>();
		methods.add(new MethodGoto());
		methods.add(new MethodJump());
		methods.add(new MethodSetPitch());
		methods.add(new MethodSetYaw());
	}
	
	@Override
	public IRobotUpgradeInstance provideUpgradeInstance(IRobot robot) {
		return new InstanceMovementUpgrade(robot);
	}

	@Override
	public String getUpgradeId() {
		return "movement";
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
