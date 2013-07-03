package openperipheral.common.robotupgrades.lazers;

import java.util.ArrayList;

import openperipheral.api.IRestriction;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;

public class MethodMaxLazerHeat implements IRobotMethod {

	public MethodMaxLazerHeat() {
	}

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
		return "getMaxLazerHeat";
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
		return ((InstanceLazersUpgrade) instance).getMaxHeat();
	}

}
