package openperipheral.integration.thaumcraft;

import java.lang.reflect.Field;

import openmods.utils.ReflectionHelper;
import openperipheral.api.*;

import com.google.common.base.Preconditions;

@Prefixed("target")
public class AdapterBrainJar implements IPeripheralAdapter {
	private static final Class<?> TILE_JAR_BRAIN_CLASS = ReflectionHelper.getClass("thaumcraft.common.tiles.TileJarBrain");

	@Override
	public Class<?> getTargetClass() {
		return TILE_JAR_BRAIN_CLASS;
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Returns the amount of XP stored in the Brain in a Jar")
	public int getXP(Object target) throws Exception
	{
		Field f = ReflectionHelper.getField(TILE_JAR_BRAIN_CLASS, "xp");
		Object o = f.get(target);

		if (o == null) return -1;
		Preconditions.checkState(o instanceof Integer, "Invalid XP in jar");
		return (Integer)o;
	}
}
