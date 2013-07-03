package openperipheral.common.robotupgrades.lazers;

import java.util.ArrayList;

import openperipheral.api.IRestriction;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;

public class MethodGetLazerCoolingPerTick implements IRobotMethod {

	public MethodGetLazerCoolingPerTick() {
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
		return "getLazerCoolingPerTick";
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
		return ((InstanceLazersUpgrade) instance).getCoolingPerTick();
	}

}
