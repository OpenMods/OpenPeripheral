package openperipheral.integration.minefactoryreloaded;

import java.util.Map;

import openmods.utils.ReflectionHelper;
import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import dan200.computer.api.IComputerAccess;

public class AdapterHarvester implements IPeripheralAdapter {
	
	private static final Class<?> HARVESTER_CLASS = ReflectionHelper.getClass(
			"powercrystals.minefactoryreloaded.tile.machine.TileEntityHarvester"
		);

	@Override
	public Class<?> getTargetClass() {
		return HARVESTER_CLASS;
	}
	
	@LuaMethod(description = "Get value of shear leaves", returnType = LuaType.BOOLEAN)
	public Boolean getShearLeaves(IComputerAccess computer, Object tileEntityHarvester){
		return getSetting(tileEntityHarvester, "silkTouch");
	}
	
	@LuaMethod(description = "Get value of harvest small mushrooms", returnType = LuaType.BOOLEAN)
	public Boolean getHarvestShrooms(IComputerAccess computer, Object tileEntityHarvester){
		return getSetting(tileEntityHarvester, "harvestSmallMushrooms");
	}
	
	@LuaMethod(description = "Set value of shear leaves", returnType = LuaType.STRING, 
		args = {
			@Arg(name = "shearLeaves", type = LuaType.BOOLEAN, description = "boolean: Shear leaves?")
		})
	public String setShearLeaves(IComputerAccess computer, Object tileEntityHarvester, boolean shearLeaves){
		// TODO:
		// Waiting on Skyboy... Property is not currently exposed.
		return "Not implemented yet, sorry :(";
	}
	
	@LuaMethod(description = "Set value of harvest small mushrooms", returnType = LuaType.STRING,
		args = {
			@Arg(name = "harvestShrooms", type = LuaType.BOOLEAN, description = "boolean: Harvest shrooms?")
		})
	public String setHarvestShrooms(IComputerAccess computer, Object tileEntityHarvester, boolean harvestShrooms){
		// TODO:
		// Waiting on Skyboy... Property is not currently exposed.
		return "Not implemented yet, sorry :(";
	}
	
	
	private Boolean getSetting(Object tileEntityHarvester, String key) {
		Map<String, Boolean> allSettings = ReflectionHelper.call(tileEntityHarvester, "getSettings");
		Boolean setting = allSettings.get(key);
		return setting;
	}

}
