package openperipheral.common.integration.gregtech;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.script.ScriptException;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IMethodDefinition;
import openperipheral.api.IRestriction;

public class DefinitionMetaMethod implements IMethodDefinition {
	
	private IGregTechMetaMethodCall callback;
	private String luaName;
	private Class[] requiredParameters;
	
	public DefinitionMetaMethod(String luaName, Class[] requiredParameters, IGregTechMetaMethodCall callback) {
		this.callback = callback;
		this.luaName = luaName;
		this.requiredParameters = requiredParameters;
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
		return requiredParameters;
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
	public Object execute(TileEntity tile, Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ScriptException {
		Object metaTileEntity = DefinitionMetaClass.getMetaTileEntity(tile);
		if (metaTileEntity != null) {
			return callback.execute(this, metaTileEntity, args);
		}
		return null;
	}

}
