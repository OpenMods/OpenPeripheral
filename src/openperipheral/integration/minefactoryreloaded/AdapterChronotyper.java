package openperipheral.integration.minefactoryreloaded;

import dan200.computer.api.IComputerAccess;
import openmods.utils.ReflectionHelper;
import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;

public class AdapterChronotyper implements IPeripheralAdapter {
//TileEntityChronotyper
	private static final Class<?> CHRONOTYPER_CLASS = ReflectionHelper.getClass(
			"powercrystals.minefactoryreloaded.tile.machine.TileEntityChronotyper"
		);

	@Override
	public Class<?> getTargetClass() {
		return CHRONOTYPER_CLASS;
	}
	
	@LuaMethod(description = "Should adults be moved?", returnType = LuaType.BOOLEAN)
	public boolean getMoveAdults(IComputerAccess computer, Object tileEntityChronotyper){
		return ReflectionHelper.call(tileEntityChronotyper, "getMoveOld");
	}
	
	@LuaMethod(description = "Set wheather adults should be moved", returnType = LuaType.VOID,
			args = {
				@Arg(name = "adults", type = LuaType.BOOLEAN, description = "boolean: Move adults?")
			})
	public void setMoveAdults(IComputerAccess computer, Object tileEntityChronotyper, boolean adults){
		ReflectionHelper.call(tileEntityChronotyper, "setMoveOld", ReflectionHelper.primitive(adults));
	}

}
