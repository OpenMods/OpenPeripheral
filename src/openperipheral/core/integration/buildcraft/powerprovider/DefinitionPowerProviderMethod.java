package openperipheral.core.integration.buildcraft.powerprovider;

import java.util.ArrayList;
import java.util.HashMap;

import openperipheral.api.IRestriction;
import openperipheral.core.interfaces.IPeripheralMethodDefinition;
import openperipheral.core.util.ReflectionHelper;
import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;

public class DefinitionPowerProviderMethod implements IPeripheralMethodDefinition {

	private String name;
	private String luaName;
	
	public DefinitionPowerProviderMethod(String name) {
		this(name, name);
	}
	
	public DefinitionPowerProviderMethod(String name, String luaName) {
		this.name = name;
		this.luaName = luaName;
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
		return new Class[] { };
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public String getLuaName() {
		return luaName;
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
		if (tile instanceof IPowerReceptor) {
			IPowerReceptor receptor = (IPowerReceptor) tile;
			IPowerProvider provider = receptor.getPowerProvider();
			return ReflectionHelper.callMethod(false, "", provider, new String[] { name });
		}
		return null;
	}

}
