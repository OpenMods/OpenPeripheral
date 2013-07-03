package openperipheral.common.robotupgrades.movement;

import java.util.ArrayList;

import openperipheral.api.IRestriction;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;

public class MethodGoto implements IRobotMethod {
	
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
		return "goto";
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public Class[] getRequiredParameters() {
		return new Class[] { int.class, int.class, int.class };
	}

	@Override
	public Object execute(IRobotUpgradeInstance instance, Object[] args) throws Exception {
		int x = (Integer) args[0];
		int y = (Integer) args[1];
		int z = (Integer) args[2];
		InstanceMovementUpgrade movementUpgrade = (InstanceMovementUpgrade) instance;
		movementUpgrade.setTargetLocation(x, y, z);
		return true;
	}
}
