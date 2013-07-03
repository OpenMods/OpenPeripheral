package openperipheral.common.integration.sgcraft.method;

import java.util.ArrayList;
import java.util.HashMap;

import openperipheral.api.IRestriction;
import openperipheral.common.interfaces.IPeripheralMethodDefinition;
import openperipheral.common.util.ReflectionHelper;

public class SGCraftIsConnected implements IPeripheralMethodDefinition {

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
		return new Class[] { };
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public String getLuaName() {
		return "isConnected";
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public boolean needsSanitize() {
		return false;
	}

	@Override
	public ArrayList<IRestriction> getRestrictions(int index) {
		return null;
	}

	@Override
	public Object execute(Object tile, Object[] args) throws Exception {
		return (Boolean) ReflectionHelper.callMethod("", tile, new String[] { "isConnected" });
	}

}
