package openperipheral.integration.minefactoryreloaded;

import openmods.utils.ReflectionHelper;
import openperipheral.api.*;

public class AdapterAutoEnchanter implements IPeripheralAdapter {
	// TileEntityAutoEnchanter
	private static final Class<?> AUTOENCHANTER_CLASS = ReflectionHelper.getClass(
			"powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoEnchanter"
			);

	@Override
	public Class<?> getTargetClass() {
		return AUTOENCHANTER_CLASS;
	}

	@LuaMethod(description = "Get target level of enchantment", returnType = LuaType.NUMBER)
	public int getTargetLevel(Object tileEntityAutoEnchanter) {
		return ReflectionHelper.call(tileEntityAutoEnchanter, "getTargetLevel");
	}

	@LuaMethod(description = "Set the target level of enchantment (1-30)", returnType = LuaType.VOID,
			args = {
					@Arg(name = "level", type = LuaType.NUMBER, description = "number: Target enchantment level")
			})
	public void setTargetLevel(Object tileEntityAutoEnchanter, int level) {
		ReflectionHelper.call(tileEntityAutoEnchanter, "setTargetLevel", ReflectionHelper.primitive(level));
	}

}
