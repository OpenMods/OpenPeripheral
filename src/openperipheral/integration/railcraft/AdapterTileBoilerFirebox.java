package openperipheral.integration.railcraft;

import openmods.utils.ReflectionHelper;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;

public class AdapterTileBoilerFirebox implements IPeripheralAdapter {
	private static final Class<?> CLAZZ = ReflectionHelper.getClass("mods.railcraft.common.blocks.machine.beta.TileBoilerFirebox");
	
	@Override
	public Class<?> getTargetClass() {
		return CLAZZ;
	}
	
	@LuaMethod(description = "Get whether the boiler is active or not", returnType = LuaType.BOOLEAN)
	public boolean isBurning(Object target) {
		return ReflectionHelper.call(target, "isBurning");
	}
	
	@LuaMethod(description = "Get the temperature of the boiler", returnType = LuaType.NUMBER)
	public float getTemperature(Object target) {
		return ReflectionHelper.call(target, "getTemperature");
	}
}
