package openperipheral.integration.minefactoryreloaded;

import openmods.utils.ReflectionHelper;
import openperipheral.api.*;

public class AdapterAutoAnvil implements IPeripheralAdapter {

	private static final Class<?> AUTOANVIL_CLASS = ReflectionHelper.getClass(
			"powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoAnvil"
			);

	@Override
	public Class<?> getTargetClass() {
		return AUTOANVIL_CLASS;
	}

	@LuaMethod(description = "Get value of repair only toggle", returnType = LuaType.BOOLEAN)
	public boolean getRepairOnly(Object tileEntityAutoAnvil) {
		return ReflectionHelper.call(tileEntityAutoAnvil, "getRepairOnly");
	}

	@LuaMethod(description = "Set the value of repair only toggle", returnType = LuaType.VOID,
			args = {
					@Arg(name = "repair", type = LuaType.BOOLEAN, description = "boolean: Repair only?")
			})
	public void setRepairOnly(Object tileEntityAutoAnvil, boolean repair) {
		// NOTE: This doesn't seem to always work as expected. Consulting Skyboy
		// about it.
		ReflectionHelper.call(tileEntityAutoAnvil, "setRepairOnly", ReflectionHelper.primitive(repair));
	}

}
