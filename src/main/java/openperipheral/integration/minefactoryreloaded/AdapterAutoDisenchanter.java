package openperipheral.integration.minefactoryreloaded;

import openmods.utils.ReflectionHelper;
import openperipheral.api.*;

public class AdapterAutoDisenchanter implements IPeripheralAdapter {

	private static final Class<?> AUTODISENCHANTER_CLASS = ReflectionHelper.getClass(
			"powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoDisenchanter"
			);

	@Override
	public Class<?> getTargetClass() {
		return AUTODISENCHANTER_CLASS;
	}

	@LuaMethod(description = "Get value of repeat disenchant toggle", returnType = LuaType.BOOLEAN)
	public boolean getRepeat(Object tileEntityAutoDisenchanter) {
		return ReflectionHelper.call(tileEntityAutoDisenchanter, "getRepeatDisenchant");
	}

	@LuaMethod(description = "Set the value of repeat disenchant toggle", returnType = LuaType.VOID,
			args = {
					@Arg(name = "repeat", type = LuaType.BOOLEAN, description = "boolean: Repeat disenchant?")
			})
	public void setRepeat(Object tileEntityAutoDisenchanter, boolean repeat) {
		ReflectionHelper.call(tileEntityAutoDisenchanter, "setRepeatDisenchant", ReflectionHelper.primitive(repeat));
	}

}
