package openperipheral.common.integration.thermalexpansion;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IMethodDefinition;
import openperipheral.api.IRestriction;
import openperipheral.common.util.ReflectionHelper;

public class TesseractGetModeMethodDefinition implements IMethodDefinition {

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
