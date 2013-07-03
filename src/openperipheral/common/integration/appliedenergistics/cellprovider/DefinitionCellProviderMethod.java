package openperipheral.common.integration.appliedenergistics.cellprovider;

import java.util.ArrayList;
import java.util.HashMap;

import openperipheral.api.IRestriction;
import openperipheral.common.interfaces.IPeripheralMethodDefinition;
import openperipheral.common.util.ReflectionHelper;
import appeng.api.me.tiles.ICellProvider;
import appeng.api.me.util.IMEInventoryHandler;

public class DefinitionCellProviderMethod implements IPeripheralMethodDefinition {

	private String name;
	private String luaName;
	private Class[] required;
	
	public DefinitionCellProviderMethod(String name, String luaName, Class ... required) {
		this.name = name;
		this.luaName = luaName;
		this.required = required;
	}
	
	public DefinitionCellProviderMethod(String name, Class ... required) {
		this(name, name, required);
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
		return required;
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
		return true;
	}

	@Override
	public ArrayList<IRestriction> getRestrictions(int index) {
		return null;
	}

	@Override
	public Object execute(Object tile, Object[] args) throws Exception {
		if (tile instanceof ICellProvider) {
			IMEInventoryHandler handler = ((ICellProvider) tile).provideCell();
			return ReflectionHelper.callMethod(false, "", handler, new String[] { name }, args);	
		}
		return null;
	}

}
