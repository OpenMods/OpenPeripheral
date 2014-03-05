package openperipheral.integration.thaumcraft;

import openmods.utils.ReflectionHelper;
import openperipheral.api.*;
import openperipheral.util.FieldAccessHelpers;

@Prefixed("target")
public class AdapterBrainJar implements IPeripheralAdapter {
	private static final Class<?> TILE_JAR_BRAIN_CLASS = ReflectionHelper.getClass("thaumcraft.common.tiles.TileJarBrain");

	@Override
	public Class<?> getTargetClass() {
		return TILE_JAR_BRAIN_CLASS;
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Returns the amount of XP stored in the Brain in a Jar")
	public int getXP(Object target) {
		return FieldAccessHelpers.getIntField(TILE_JAR_BRAIN_CLASS, target, "xp");
	}
}
