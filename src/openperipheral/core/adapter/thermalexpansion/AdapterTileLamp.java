package openperipheral.core.adapter.thermalexpansion;

import dan200.computer.api.IComputerAccess;
import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.api.Arg;
import openperipheral.core.util.ReflectionHelper;

public class AdapterTileLamp implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return ReflectionHelper.getClass("thermalexpansion.block.lamp.TileLamp");
	}
	
	@LuaMethod(description="Sets the colour of the lamp.", returnType=LuaType.BOOLEAN, args={
			@Arg(description="The colour you want to set to (in RGB hexadecimal 0xRRGGBB)", type=LuaType.NUMBER)
	})
	public boolean setColor(IComputerAccess computer, TileEntity tile, int colour) {
		try {
			return (Boolean) ReflectionHelper.callMethod(getTargetClass(), tile, new String[]{"setColor"}, new Object[]{colour});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
