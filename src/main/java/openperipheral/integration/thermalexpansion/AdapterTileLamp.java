package openperipheral.integration.thermalexpansion;

import net.minecraft.tileentity.TileEntity;
import openmods.utils.ReflectionHelper;
import openperipheral.api.*;

public class AdapterTileLamp implements IPeripheralAdapter {

	private static final Class<?> CLAZZ = ReflectionHelper.getClass("thermalexpansion.block.lamp.TileLamp");

	@Override
	public Class<?> getTargetClass() {
		return CLAZZ;
	}

	@Alias("setColour")
	@LuaCallable(returnTypes = LuaType.BOOLEAN, description = "Sets the colour of the lamp")
	public boolean setColor(TileEntity tile,
			@Arg(description = "The colour you want to set to (in RGB hexadecimal 0xRRGGBB)", type = LuaType.NUMBER, name = "color") int colour) {
		return ReflectionHelper.<Boolean> call(tile, "setColor", ReflectionHelper.primitive(colour));
	}

}
