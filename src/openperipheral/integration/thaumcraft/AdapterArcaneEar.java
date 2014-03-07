package openperipheral.integration.thaumcraft;

import java.lang.reflect.Field;

import openmods.utils.ReflectionHelper;
import openperipheral.api.*;
import openperipheral.util.FieldAccessHelpers;

@Prefixed("target")
public class AdapterArcaneEar implements IPeripheralAdapter {
	private static final Class<?> TILE_ARCANE_EAR = ReflectionHelper.getClass("thaumcraft.common.tiles.TileSensor");

	@Override
	public Class<?> getTargetClass() {
		return TILE_ARCANE_EAR;
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "gets the note the Ear is set to")
	public byte getNote(Object target) {
		return FieldAccessHelpers.getByteField(TILE_ARCANE_EAR, target, "note");
	}

	@LuaMethod(returnType = LuaType.VOID, args = { @Arg(description = "Note to set", name = "note", type = LuaType.NUMBER, isNullable = false) }, description = "Sets the note on the ear")
	public void setNote(Object target, byte note) throws Exception {
		Field f = ReflectionHelper.getField(TILE_ARCANE_EAR, "note");
		f.set(target, note);
	}

}
