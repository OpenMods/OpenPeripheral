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
	
	private Map<String, Boolean> allSettings;

	@Override
	public Class<?> getTargetClass() {
		return HARVESTER_CLASS;
	}
	
	@LuaMethod(description = "Get value of shear leaves", returnType = LuaType.BOOLEAN)
	public Boolean getShearLeaves(IComputerAccess computer, Object tileEntityHarvester){
		getSettings(tileEntityHarvester);
		return allSettings.get("silkTouch");
	}
	
	@LuaMethod(description = "Get value of harvest small mushrooms", returnType = LuaType.BOOLEAN)
	public Boolean getHarvestShrooms(IComputerAccess computer, Object tileEntityHarvester){
		getSettings(tileEntityHarvester);
		return allSettings.get("harvestSmallMushrooms");
	}
	
	@LuaMethod(description = "Set value of shear leaves", returnType = LuaType.VOID, 
		args = {
			@Arg(name = "shearLeaves", type = LuaType.BOOLEAN, description = "boolean: Shear leaves?")
		})
	public void setShearLeaves(IComputerAccess computer, Object tileEntityHarvester, boolean shearLeaves){
		getSettings(tileEntityHarvester);
		allSettings.put("silkTouch", shearLeaves);
	}
	
	@LuaMethod(description = "Set value of harvest small mushrooms", returnType = LuaType.VOID,
		args = {
			@Arg(name = "harvestShrooms", type = LuaType.BOOLEAN, description = "boolean: Harvest shrooms?")
		})
	public void setHarvestShrooms(IComputerAccess computer, Object tileEntityHarvester, boolean harvestShrooms){
		getSettings(tileEntityHarvester);
		allSettings.put("harvestSmallMushrooms", harvestShrooms);
	}
	
	
	private void getSettings(Object tileEntityHarvester) {
		allSettings = ReflectionHelper.call(tileEntityHarvester, "getSettings");
	}

}
