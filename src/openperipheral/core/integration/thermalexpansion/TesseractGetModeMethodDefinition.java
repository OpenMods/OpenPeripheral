package openperipheral.core.integration.thermalexpansion;

import java.util.ArrayList;
import java.util.HashMap;

import openperipheral.api.IRestriction;
import openperipheral.core.interfaces.IPeripheralMethodDefinition;
import openperipheral.core.util.ReflectionHelper;

public class TesseractGetModeMethodDefinition implements IPeripheralMethodDefinition {

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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getLuaName() {
		return "getMode";
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
	public Object execute(Object tile, Object[] args) throws Exception {
		int mode = new Byte((Byte)(ReflectionHelper.getProperty("", tile, "mode"))).intValue();
		return TEModule.tesseractModes[mode];
	}

}
