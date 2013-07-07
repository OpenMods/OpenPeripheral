package openperipheral.core.integration.gregtech;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.script.ScriptException;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IRestriction;
import openperipheral.core.interfaces.IPeripheralMethodDefinition;

public class DefinitionMetaMethod implements IPeripheralMethodDefinition {
	
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
	public Object execute(Object tile, Object[] args) throws Exception {
		if (tile instanceof TileEntity) {
			Object metaTileEntity = DefinitionMetaClass.getMetaTileEntity((TileEntity)tile);
			if (metaTileEntity != null) {
				return callback.execute(this, metaTileEntity, args);
			}
		}
		return null;
	}

}
