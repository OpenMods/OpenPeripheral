package openperipheral.integration.minefactoryreloaded;

import dan200.computer.api.IComputerAccess;
import openmods.utils.ReflectionHelper;
import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;

public class AdapterAutoJukebox implements IPeripheralAdapter {

	private static final Class<?> AUTOJUKEBOX_CLASS = ReflectionHelper.getClass(
			"powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoJukebox"
		);

	@Override
	public Class<?> getTargetClass() {
		return AUTOJUKEBOX_CLASS;
	}
	
	@LuaMethod(description = "Can a disc be copied?", returnType = LuaType.BOOLEAN)
	public boolean getCanCopy(IComputerAccess computer, Object tileEntityAutoJukebox){
		return ReflectionHelper.call(tileEntityAutoJukebox, "getCanCopy");
	}
	
	@LuaMethod(description = "Set wheather a disc can be copied", returnType = LuaType.VOID,
			args = {
				@Arg(name = "copyable", type = LuaType.BOOLEAN, description = "boolean: Can be copied?")
			})
	public void setCanCopy(IComputerAccess computer, Object tileEntityAutoJukebox, boolean copyable){
		ReflectionHelper.call(tileEntityAutoJukebox, "setCanCopy", ReflectionHelper.primitive(copyable));
	}
	
	@LuaMethod(description = "Can a disc be played?", returnType = LuaType.BOOLEAN)
	public boolean getCanPlay(IComputerAccess computer, Object tileEntityAutoJukebox){
		return ReflectionHelper.call(tileEntityAutoJukebox, "getCanPlay");
	}
	
	@LuaMethod(description = "Set wheather a disc can be played", returnType = LuaType.VOID,
			args = {
				@Arg(name = "playable", type = LuaType.BOOLEAN, description = "boolean: Can be played?")
			})
	public void setCanPlay(IComputerAccess computer, Object tileEntityAutoJukebox, boolean playable){
		ReflectionHelper.call(tileEntityAutoJukebox, "setCanPlay", ReflectionHelper.primitive(playable));
	}
	
	@LuaMethod(description = "Copy record", returnType = LuaType.VOID)
	public void copy(IComputerAccess computer, Object tileEntityAutoJukebox) {
		ReflectionHelper.call(tileEntityAutoJukebox, "copyRecord");
	}
	
	@LuaMethod(description = "Play record", returnType = LuaType.VOID)
	public void play(IComputerAccess computer, Object tileEntityAutoJukebox) {
		ReflectionHelper.call(tileEntityAutoJukebox, "playRecord");
	}
	
	@LuaMethod(description = "Stop record", returnType = LuaType.VOID)
	public void stop(IComputerAccess computer, Object tileEntityAutoJukebox) {
		ReflectionHelper.call(tileEntityAutoJukebox, "stopRecord");
	}

}
