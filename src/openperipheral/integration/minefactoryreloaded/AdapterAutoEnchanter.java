package openperipheral.integration.minefactoryreloaded;

import dan200.computer.api.IComputerAccess;
import openmods.utils.ReflectionHelper;
import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;

public class AdapterAutoEnchanter implements IPeripheralAdapter {
//TileEntityAutoEnchanter
	private static final Class<?> AUTOENCHANTER_CLASS = ReflectionHelper.getClass(
			"powercrystals.minefactoryreloaded.tile.machine.TileEntityAutoEnchanter"
		);

	@Override
	public Class<?> getTargetClass() {
		return AUTOENCHANTER_CLASS;
	}
	
	@LuaMethod(description = "Get target level of enchantment", returnType = LuaType.NUMBER)
	public int getTargetLevel(IComputerAccess computer, Object tileEntityAutoEnchanter){
		return ReflectionHelper.call(tileEntityAutoEnchanter, "getTargetLevel");
	}
	
	@LuaMethod(description = "Set the target level of enchantment (1-30)", returnType = LuaType.VOID,
			args = {
				@Arg(name = "level", type = LuaType.NUMBER, description = "number: Target enchantment level")
			})
	public void setTargetLevel(IComputerAccess computer, Object tileEntityAutoEnchanter, int level){
		ReflectionHelper.call(tileEntityAutoEnchanter, "setTargetLevel", ReflectionHelper.primitive(level));
	}

}
