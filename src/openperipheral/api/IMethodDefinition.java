package openperipheral.api;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.tileentity.TileEntity;

public interface IMethodDefinition {
	public HashMap<Integer, String> getReplacements();
	public String getPostScript();
	public boolean getCauseTileUpdate();
	public Class[] getRequiredParameters();
	public boolean isInstant();
	public String getLuaName();
	public boolean isValid();
	public boolean needsSanitize();
	public ArrayList<IRestriction> getRestrictions(int index);
	public Object execute(Object target, Object[] args) throws Exception;
}
