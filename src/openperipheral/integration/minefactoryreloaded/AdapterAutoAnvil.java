package openperipheral.integration.minefactoryreloaded;

import dan200.computer.api.IComputerAccess;
import openmods.utils.ReflectionHelper;
import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;

public class AdapterAutoAnvil implements IPeripheralAdapter {

	private static final Class<?> AUTOANVIL_CLASS = ReflectionHelper.getClass(
			"powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoAnvil"
		);

	@Override
	public Class<?> getTargetClass() {
		return AUTOANVIL_CLASS;
	}
	
	@LuaMethod(description = "Get value of repair only toggle", returnType = LuaType.BOOLEAN)
	public boolean getRepairOnly(IComputerAccess computer, Object tileEntityAutoAnvil){
		return ReflectionHelper.call(tileEntityAutoAnvil, "getRepairOnly");
	}
	
	@LuaMethod(description = "Set the value of repair only toggle", returnType = LuaType.VOID,
			args = {
				@Arg(name = "repair", type = LuaType.BOOLEAN, description = "boolean: Repair only?")
			})
	public void setRepairOnly(IComputerAccess computer, Object tileEntityAutoAnvil, boolean repair){
		// NOTE: This doesn't seem to always work as expected. Consulting Skyboy about it.
		ReflectionHelper.call(tileEntityAutoAnvil, "setRepairOnly", ReflectionHelper.primitive(repair));
	}

}
