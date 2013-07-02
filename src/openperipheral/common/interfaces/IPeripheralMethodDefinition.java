package openperipheral.common.interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import openperipheral.api.IRestriction;


import net.minecraft.tileentity.TileEntity;

public interface IPeripheralMethodDefinition {
	public HashMap<Integer, String> getReplacements();
	public String getPostScript();
	public boolean getCauseTileUpdate();
	public boolean isValid();
	public boolean needsSanitize();
	public ArrayList<IRestriction> getRestrictions(int index);
	public String getLuaName();
	public boolean isInstant();
	public Class[] getRequiredParameters();
	public Object execute(Object target, Object[] args) throws Exception;
}
