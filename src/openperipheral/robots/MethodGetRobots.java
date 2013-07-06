package openperipheral.robots;

import java.util.ArrayList;
import java.util.HashMap;

import openperipheral.api.IRestriction;
import openperipheral.core.interfaces.IPeripheralMethodDefinition;
import openperipheral.robots.block.TileEntityRobot;

public class MethodGetRobots implements IPeripheralMethodDefinition {

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
	public boolean isValid() {
		return true;
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
		return "getAvailableRobots";
	}

	@Override
	public boolean isInstant() {
		return true;
	}

	@Override
	public Class[] getRequiredParameters() {
		return null;
	}

	@Override
	public Object execute(Object target, Object[] args) throws Exception {
		TileEntityRobot tile = (TileEntityRobot) target;
		return tile.getRobotIds();
	}

}
