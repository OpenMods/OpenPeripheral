package openperipheral.common.robotupgrades;

import java.util.ArrayList;
import java.util.HashMap;

import openperipheral.api.IRestriction;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.common.interfaces.IPeripheralMethodDefinition;

/**
 * This class wraps a "Robot method" in a PeripheralMethodDefinition.
 * They're quite similar, except the peripheral method exposes additional
 * things that don't belong in the public API
 *
 */

public class RobotPeripheralMethod implements IPeripheralMethodDefinition {

	private IRobotMethod robotMethodDefinition;
	
	public RobotPeripheralMethod(IRobotMethod methodDefinition) {
		this.robotMethodDefinition = methodDefinition;
	}
	
	@Override
	public boolean needsSanitize() {
		return robotMethodDefinition.needsSanitize();
	}

	@Override
	public ArrayList<IRestriction> getRestrictions(int index) {
		return robotMethodDefinition.getRestrictions(index);
	}

	@Override
	public String getLuaName() {
		return robotMethodDefinition.getLuaName();
	}

	/**
	 * Add the robot ID to the start of the required parameters. We don't
	 * really need upgrade methods to need to define that
	 */
	@Override
	public Class[] getRequiredParameters() {
		Class[] methodParams = robotMethodDefinition.getRequiredParameters();
		if (methodParams == null) {
			methodParams = new Class[]{};
		}
		Class[] paramsWithRobotId = new Class[methodParams.length + 1];
		paramsWithRobotId[0] = int.class;
		for (int i = 0; i < methodParams.length; i++) {
			paramsWithRobotId[i+1] = methodParams[i];
		}
		return paramsWithRobotId;
	}

	@Override
	public Object execute(Object target, Object[] args) throws Exception {
		return robotMethodDefinition.execute((IRobotUpgradeInstance) target, args);
	}

	@Override
	public HashMap<Integer, String> getReplacements() {
		return null;
	}

	@Override
	public String getPostScript() {
		return null;
	}

	@Override
	public boolean getCauseTileUpdate() {
		return false;
	}

	@Override
	public boolean isInstant() {
		return robotMethodDefinition.isInstant();
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
