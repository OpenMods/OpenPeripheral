package openperipheral.integration.thaumcraft;

import java.lang.reflect.Field;

import openmods.utils.ReflectionHelper;
import openperipheral.api.*;

import com.google.common.base.Preconditions;

@Prefixed("target")
public class AdapterArcaneEar implements IPeripheralAdapter {
	private static final Class<?> TILE_ARCANE_EAR = ReflectionHelper.getClass("thaumcraft.common.tiles.TileSensor");

	@Override
	public Class<?> getTargetClass() {
		return TILE_ARCANE_EAR;
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "gets the note the Ear is set to")
	public int getNote(Object target) throws Exception
	{
		Field f = ReflectionHelper.getField(TILE_ARCANE_EAR, "note");
		Object o = f.get(target);

		if (o == null) return -1;
		Preconditions.checkState(o instanceof Byte, "Arcane ear is broken");
		return (Byte)o;
	}

	@LuaMethod(returnType = LuaType.VOID, args = { @Arg(description = "Note to set", name = "note", type = LuaType.NUMBER, isNullable = false) }, description = "Sets the note on the ear")
	public void setNote(Object target, double note) throws Exception
	{
		Byte set = (byte)note;
		Field f = ReflectionHelper.getField(TILE_ARCANE_EAR, "note");
		f.set(target, set);
	}

}
