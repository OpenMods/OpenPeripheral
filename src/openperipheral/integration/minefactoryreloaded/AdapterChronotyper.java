package openperipheral.integration.minefactoryreloaded;

import openmods.utils.ReflectionHelper;
import openperipheral.api.*;

public class AdapterChronotyper implements IPeripheralAdapter {
	// TileEntityChronotyper
	private static final Class<?> CHRONOTYPER_CLASS = ReflectionHelper.getClass(
			"powercrystals.minefactoryreloaded.tile.machine.TileEntityChronotyper"
			);

	@Override
	public Class<?> getTargetClass() {
		return CHRONOTYPER_CLASS;
	}

	@LuaMethod(description = "Should adults be moved?", returnType = LuaType.BOOLEAN)
	public boolean getMoveAdults(Object tileEntityChronotyper) {
		return ReflectionHelper.call(tileEntityChronotyper, "getMoveOld");
	}

	@LuaMethod(description = "Set wheather adults should be moved", returnType = LuaType.VOID,
			args = {
					@Arg(name = "adults", type = LuaType.BOOLEAN, description = "boolean: Move adults?")
			})
	public void setMoveAdults(Object tileEntityChronotyper, boolean adults) {
		ReflectionHelper.call(tileEntityChronotyper, "setMoveOld", ReflectionHelper.primitive(adults));
	}

}
