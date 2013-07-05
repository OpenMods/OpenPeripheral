package openperipheral.core.integration.sgcraft;

import java.util.ArrayList;
import java.util.HashMap;

import openperipheral.api.IRestriction;
import openperipheral.core.interfaces.IPeripheralMethodDefinition;
import openperipheral.core.restriction.RestrictionLength;
import openperipheral.core.util.ReflectionHelper;

public class MethodConnect implements IPeripheralMethodDefinition {

	private ArrayList<IRestriction> restrictions;
	
	public MethodConnect() {
		restrictions = new ArrayList<IRestriction>();
		restrictions.add(new RestrictionLength(7));
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
	public Class[] getRequiredParameters() {
		return new Class[] { String.class };
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public String getLuaName() {
		return "connect";
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
		return restrictions;
	}

	@Override
	public Object execute(Object tile, Object[] args) throws Exception {
		String homeAddress = (String) ReflectionHelper.callMethod("", tile, new String[] { "findHomeAddress" });
		Object targetStargate = ReflectionHelper.callMethod(false, "gcewing.sg.SGAddressing", null, new String[] { "findAddressedStargate" }, args[0]);
		if (targetStargate == null) {
			throw new Exception("Unable to find target gate");
		}
		if (targetStargate == tile) {
			throw new Exception("You can not connect to yourself");
		}
		Object state = ReflectionHelper.getProperty("", targetStargate, "state");
		int requiredFuel = (Integer)ReflectionHelper.getProperty("", tile, "fuelToOpen");
		boolean reloaded = (Boolean) ReflectionHelper.callMethod("", tile, new String[] { "reloadFuel" }, requiredFuel);
		if (!reloaded) {
			throw new Exception("Not enough fuel");
		}
		ReflectionHelper.callMethod("", tile, new String[] { "startDiallingStargate" }, args[0], targetStargate, true);
		ReflectionHelper.callMethod("", targetStargate, new String[] { "startDiallingStargate" }, homeAddress, tile, false);
		return true;
	}

}
