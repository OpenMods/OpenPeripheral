package openperipheral.integration.minefactoryreloaded;

import java.util.logging.Logger;

import openmods.utils.ReflectionHelper;
import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import dan200.computer.api.IComputerAccess;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AdapterAutoSpawner implements IPeripheralAdapter {
	private static final Class<?> AUTOSPAWNER_CLASS = ReflectionHelper.getClass(
			"powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoSpawner"
		);

	@Override
	public Class<?> getTargetClass() {
		return AUTOSPAWNER_CLASS;
	}
	
	@LuaMethod(description = "Get value of spawn exact copy toggle", returnType = LuaType.BOOLEAN)
	public boolean getSpawnExact(IComputerAccess computer, Object tileEntityAutoSpawner){
		return ReflectionHelper.call(tileEntityAutoSpawner, "getSpawnExact");
	}
	
	@LuaMethod(description = "Set the value of spawn exact copy", returnType = LuaType.VOID,
			args = {
				@Arg(name = "spawnExact", type = LuaType.BOOLEAN, description = "boolean: Spawn Exact Copy?")
			})
	public void setSpawnExact(IComputerAccess computer, Object tileEntityAutoSpawner, boolean spawnExact){
		ReflectionHelper.call(tileEntityAutoSpawner, "setSpawnExact", ReflectionHelper.primitive(spawnExact));
	}

}
