package openperipheral.common.robotupgrades.movement;

import java.util.ArrayList;

import net.minecraft.entity.EntityCreature;
import openperipheral.api.IRestriction;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;

public class MethodSetYaw implements IRobotMethod {

	@Override
	public boolean needsSanitize() {
		return true;
	}

	@Override
	public ArrayList<IRestriction> getRestrictions(int index) {
		return null;
	}

	@Override
	public String getLuaName() {
		return "setYaw";
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public Class[] getRequiredParameters() {
		return new Class[] { double.class };
	}

	@Override
	public Object execute(IRobotUpgradeInstance instance, Object[] args) throws Exception {
		IRobot robot = ((InstanceMovementUpgrade)instance).getRobot();
		EntityCreature creature = robot.getEntity();
		double direction = (Double)args[0];
		float fDirection = (float) direction;
		// TODO setyaw
		return true;
	}

}
