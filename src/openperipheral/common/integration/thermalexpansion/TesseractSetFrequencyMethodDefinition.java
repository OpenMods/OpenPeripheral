package openperipheral.common.integration.thermalexpansion;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IRestriction;
import openperipheral.common.interfaces.IPeripheralMethodDefinition;
import openperipheral.common.util.ReflectionHelper;

public class TesseractSetFrequencyMethodDefinition implements IPeripheralMethodDefinition {

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
		return new Class[] { Integer.class };
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public String getLuaName() {
		return "setFrequency";
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
		ReflectionHelper.callMethod(false, "", tile, new String[] { "removeFromRegistry" });
		ReflectionHelper.setProperty("", tile, args[0], "frequency");
		ReflectionHelper.callMethod(false, "", tile, new String[] { "addToRegistry" });
		return true;
	}

}
