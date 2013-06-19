package openperipheral.common.integration.thermalexpansion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IMethodDefinition;
import openperipheral.api.IRestriction;
import openperipheral.common.restriction.RestrictionChoice;
import openperipheral.common.util.ReflectionHelper;

public class TesseractSetModeMethodDefinition implements IMethodDefinition {

	private ArrayList<IRestriction> restrictions;
	
	public TesseractSetModeMethodDefinition() {
		restrictions = new ArrayList<IRestriction>();
		restrictions.add(new RestrictionChoice(TEModule.tesseractModes));
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
		return true;
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
		return "setMode";
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
	public Object execute(TileEntity tile, Object[] args) throws Exception {
		int mode = Arrays.asList(TEModule.tesseractModes).indexOf(args[0]);
		ReflectionHelper.callMethod(false, "", tile, new String[] { "removeFromRegistry" });
		ReflectionHelper.setProperty("", tile, (byte)mode, "mode");
		ReflectionHelper.callMethod(false, "", tile, new String[] { "addToRegistry" });
		return true;
	}

}
