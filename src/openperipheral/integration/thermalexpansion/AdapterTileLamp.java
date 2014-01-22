package openperipheral.integration.thermalexpansion;

import net.minecraft.tileentity.TileEntity;
import openperipheral.api.*;
import openperipheral.util.ReflectionHelper;
import dan200.computer.api.IComputerAccess;

public class AdapterTileLamp implements IPeripheralAdapter {

	private static final Class<?> CLAZZ = ReflectionHelper.getClass("thermalexpansion.block.lamp.TileLamp");

	@Override
	public Class<?> getTargetClass() {
		return CLAZZ;
	}

	@LuaMethod(description = "Sets the colour of the lamp.", returnType = LuaType.BOOLEAN, onTick = false, args = {
			@Arg(description = "The colour you want to set to (in RGB hexadecimal 0xRRGGBB)", type = LuaType.NUMBER)
	})
	public boolean setColor(IComputerAccess computer, TileEntity tile, int colour) {
		try {
			return ReflectionHelper.<Boolean> call(tile, "setColor", ReflectionHelper.primitive(colour));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@LuaMethod(description = "Sets the colour of the lamp.", returnType = LuaType.BOOLEAN, onTick = false, args = {
			@Arg(description = "The colour you want to set to (in RGB hexadecimal 0xRRGGBB)", type = LuaType.NUMBER)
	})
	public boolean setColour(IComputerAccess computer, TileEntity tile, int colour) {
		return setColor(computer, tile, colour);
	}

}
