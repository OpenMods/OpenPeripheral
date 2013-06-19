package openperipheral.common.integration.buildcraft.engine;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IMethodDefinition;
import openperipheral.api.IRestriction;
import openperipheral.common.util.ReflectionHelper;

public class DefinitionCurrentOutputMethod implements IMethodDefinition {

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
		return null;
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public String getLuaName() {
		return "getCurrentOutput";
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
	public Object execute(TileEntity tile, Object[] args) throws Exception {
		Object engine = ReflectionHelper.callMethod(false, "", tile, new String[] { "getEngine" });
		if (engine != null) {
			return ReflectionHelper.callMethod(false, "", engine, new String[] { "getCurrentOutput" });
		}
		return null;
	}

}
