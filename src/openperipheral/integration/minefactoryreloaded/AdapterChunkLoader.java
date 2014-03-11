package openperipheral.integration.minefactoryreloaded;

import openmods.utils.ReflectionHelper;
import openperipheral.api.*;
import dan200.computer.api.IComputerAccess;

public class AdapterChunkLoader implements IPeripheralAdapter {

	private static final Class<?> CHUNKLOADER_CLASS = ReflectionHelper.getClass(
			"powercrystals.minefactoryreloaded.tile.machine.TileEntityChunkLoader"
			);

	@Override
	public Class<?> getTargetClass() {
		return CHUNKLOADER_CLASS;
	}

	@LuaMethod(description = "Get chunk loader radius", returnType = LuaType.NUMBER)
	public short getRadius(IComputerAccess computer, Object tileEntityChunkLoader) {
		return ReflectionHelper.call(tileEntityChunkLoader, "getRadius");
	}

	@LuaMethod(description = "Set chunk loader radius", returnType = LuaType.VOID,
			args = {
					@Arg(name = "radius", type = LuaType.NUMBER, description = "number: radius, range 0 - 49")
			})
	public void setRadius(IComputerAccess computer, Object tileEntityChunkLoader, short radius) {
		ReflectionHelper.call(tileEntityChunkLoader, "setRadius", ReflectionHelper.primitive(radius));
	}

}
