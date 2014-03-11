package openperipheral.integration.minefactoryreloaded;

import dan200.computer.api.IComputerAccess;
import openmods.utils.ReflectionHelper;
import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;

public class AdapterEjector implements IPeripheralAdapter {

	private static final Class<?> EJECTOR_CLASS = ReflectionHelper.getClass(
			"powercrystals.minefactoryreloaded.tile.machine.TileEntityEjector"
		);

	@Override
	public Class<?> getTargetClass() {
		return EJECTOR_CLASS;
	}
	
	@LuaMethod(description = "Is whitelist enabled?", returnType = LuaType.BOOLEAN)
	public boolean getIsWhitelist(IComputerAccess computer, Object tileEntityEjector){
		return ReflectionHelper.call(tileEntityEjector, "getIsWhitelist");
	}
	
	@LuaMethod(description = "Set the value of whitelist toggle", returnType = LuaType.VOID,
			args = {
				@Arg(name = "isWhitelist", type = LuaType.BOOLEAN, description = "boolean: Whitelist Only?")
			})
	public void setIsWhitelist(IComputerAccess computer, Object tileEntityEjector, boolean isWhitelist){
		ReflectionHelper.call(tileEntityEjector, "setIsWhitelist", ReflectionHelper.primitive(isWhitelist));
	}
	
	@LuaMethod(description = "Is NBT Match enabled?", returnType = LuaType.BOOLEAN)
	public boolean getMatchNBT(IComputerAccess computer, Object tileEntityEjector){
		return ReflectionHelper.call(tileEntityEjector, "getIsNBTMatch");
	}
	
	@LuaMethod(description = "Set the value of NBT Match toggle", returnType = LuaType.VOID,
			args = {
				@Arg(name = "matchNBT", type = LuaType.BOOLEAN, description = "boolean: Match NBT?")
			})
	public void setMatchNBT(IComputerAccess computer, Object tileEntityEjector, boolean matchNBT){
		ReflectionHelper.call(tileEntityEjector, "setIsNBTMatch", ReflectionHelper.primitive(matchNBT));
	}
	
	@LuaMethod(description = "Is Match Meta enabled?", returnType = LuaType.BOOLEAN)
	public boolean getMatchMeta(IComputerAccess computer, Object tileEntityEjector){
		// getIsIDMatch returns the boolean value of _ignoreDamage, so if it is true
		// then getIsIDMatch returns false. Odd naming convention handled here to 
		// represent GUI button values.
		boolean isIdMatch = ReflectionHelper.call(tileEntityEjector, "getIsIDMatch");
		return !isIdMatch;
	}
	
	@LuaMethod(description = "Set the value of Match Damage toggle", returnType = LuaType.VOID,
			args = {
				@Arg(name = "matchMeta", type = LuaType.BOOLEAN, description = "boolean: Match NBT?")
			})
	public void setMatchMeta(IComputerAccess computer, Object tileEntityEjector, boolean matchMeta){
		ReflectionHelper.call(tileEntityEjector, "setIsIDMatch", ReflectionHelper.primitive(!matchMeta));
	}

}
