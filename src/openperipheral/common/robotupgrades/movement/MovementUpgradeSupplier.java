package openperipheral.common.robotupgrades.movement;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeDefinition;
import openperipheral.api.IRobotUpgradeInstance;

public class MovementUpgradeSupplier implements IRobotUpgradeDefinition {

	ArrayList<IRobotMethod> methods;
	
	public MovementUpgradeSupplier() {
		methods = new ArrayList<IRobotMethod>();
		methods.add(new MethodGoto());
		methods.add(new MethodJump());
		methods.add(new MethodSetPitch());
		methods.add(new MethodSetYaw());
	}
	
	@Override
	public IRobotUpgradeInstance provideUpgradeInstance(IRobot robot) {
		return new MovementUpgrade(robot);
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

}
