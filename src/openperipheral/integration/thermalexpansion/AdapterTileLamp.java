package openperipheral.integration.thermalexpansion;

import net.minecraft.tileentity.TileEntity;
import openmods.utils.ReflectionHelper;
import openperipheral.api.*;
import dan200.computercraft.api.peripheral.IComputerAccess;

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
		return ReflectionHelper.<Boolean> call(tile, "setColor", ReflectionHelper.primitive(colour));
	}

	@LuaMethod(description = "Sets the colour of the lamp.", returnType = LuaType.BOOLEAN, onTick = false, args = {
			@Arg(description = "The colour you want to set to (in RGB hexadecimal 0xRRGGBB)", type = LuaType.NUMBER)
	})
	public boolean setColour(IComputerAccess computer, TileEntity tile, int colour) {
		return setColor(computer, tile, colour);
	}

}
