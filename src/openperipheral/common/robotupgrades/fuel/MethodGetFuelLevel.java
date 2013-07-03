package openperipheral.common.robotupgrades.fuel;

import java.util.ArrayList;

import openperipheral.api.IRestriction;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;

public class MethodGetFuelLevel implements IRobotMethod {

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
		return "getFuelLevel";
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public Class[] getRequiredParameters() {
		return null;
	}

	@Override
	public Object execute(IRobotUpgradeInstance instance, Object[] args) throws Exception {
		return ((InstanceFuelUpgrade)instance).getRobot().getFuelLevel();
	}

}
