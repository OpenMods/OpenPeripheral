package openperipheral.integration.enderstorage;

import net.minecraft.tileentity.TileEntity;
import openmods.utils.ColorUtils;
import openmods.utils.ColorUtils.ColorMeta;
import openmods.utils.ReflectionHelper;
import openperipheral.api.*;

import com.google.common.base.Preconditions;

public class AdapterFrequencyOwner implements IPeripheralAdapter {
	private static final Class<?> CLAZZ = ReflectionHelper.getClass("codechicken.enderstorage.common.TileFrequencyOwner");

	@Override
	public Class<?> getTargetClass() {
		return CLAZZ;
	}

	@Alias("getColours")
	@LuaCallable(returnTypes = { LuaType.NUMBER, LuaType.NUMBER, LuaType.NUMBER },
			description = "Get the colours active on this chest or tank")
	public IMultiReturn getColors(TileEntity frequencyOwner) {
		// get the current frequency
		int frequency = getFreq(frequencyOwner);
		// return a map of the frequency in ComputerCraft colour format
		return OpenPeripheralAPI.wrap(
				1 << (frequency >> 8 & 0xF),
				1 << (frequency >> 4 & 0xF),
				1 << (frequency >> 0 & 0xF));
	}

	@LuaCallable(returnTypes = { LuaType.STRING, LuaType.STRING, LuaType.STRING },
			description = "Get the colours active on this chest or tank")
	public IMultiReturn getColorNames(TileEntity frequencyOwner) {
		int frequency = getFreq(frequencyOwner);
		return OpenPeripheralAPI.wrap(
				colorToName(frequency >> 8 & 0xF),
				colorToName(frequency >> 4 & 0xF),
				colorToName(frequency >> 0 & 0xF));
	}

	@Alias("setColours")
	@LuaMethod(returnType = LuaType.VOID, onTick = false, description = "Set the frequency of this chest or tank", args = {
			@Arg(name = "color_left", type = LuaType.NUMBER, description = "The first color"),
			@Arg(name = "color_middle", type = LuaType.NUMBER, description = "The second color"),
			@Arg(name = "color_right", type = LuaType.NUMBER, description = "The third color")
	})
	public void setColors(TileEntity frequencyOwner, int colorLeft, int colorMiddle, int colorRight) {
		// transform the ComputerCraft colours (2^n) into the range 0-15 And
		// validate they're within this range
		int high = parseComputerCraftColor(colorLeft);
		int med = parseComputerCraftColor(colorMiddle);
		int low = parseComputerCraftColor(colorRight);

		int frequency = ((high & 0xF) << 8) + ((med & 0xF) << 4) + (low & 0xF);
		setFreq(frequencyOwner, frequency);
	}

	@LuaCallable(description = "Set the frequency of this chest or tank")
	public void setColorNames(TileEntity frequencyOwner,
			@Arg(name = "color_left", type = LuaType.STRING) String colorLeft,
			@Arg(name = "color_middle", type = LuaType.STRING) String colorMiddle,
			@Arg(name = "color_right", type = LuaType.STRING) String colorRight) {

		int high = parseColorName(colorLeft);
		int med = parseColorName(colorMiddle);
		int low = parseColorName(colorRight);
		// convert the three colours into a single colour
		int frequency = ((high & 0xF) << 8) + ((med & 0xF) << 4) + (low & 0xF);
		// set the TE's frequency to the new colours
		setFreq(frequencyOwner, frequency);
	}

	@LuaMethod(
			returnType = LuaType.TABLE, onTick = false, description = "Get the frequency of this chest or tank")
	public int getFrequency(TileEntity frequencyOwner) {
		return getFreq(frequencyOwner);
	}

	@LuaMethod(returnType = LuaType.VOID, onTick = false, description = "Set the frequency of this chest or tank",
			args = {
					@Arg(name = "frequency", type = LuaType.NUMBER, description = "A single color that represents all three colours on this chest or tank") })
	public void setFrequency(TileEntity frequencyOwner, int frequency) {
		setFreq(frequencyOwner, frequency);
	}

	private static int getFreq(TileEntity frequencyOwner) {
		return (Integer)ReflectionHelper.getProperty(CLAZZ, frequencyOwner, "freq");
	}

	private static void setFreq(TileEntity frequencyOwner, int frequency) {
		Preconditions.checkElementIndex(frequency, 4096, "frequency");
		ReflectionHelper.call(frequencyOwner, "setFreq", ReflectionHelper.primitive(frequency));
	}

	private static int parseComputerCraftColor(int bitmask) {
		ColorMeta meta = ColorUtils.bitmaskToColor(bitmask);
		Preconditions.checkNotNull(meta, "Invalid color %sb", Integer.toBinaryString(bitmask));
		return meta.vanillaId;
	}

	private static int parseColorName(String name) {
		ColorMeta meta = ColorUtils.nameToColor(name);
		Preconditions.checkNotNull(meta, "Invalid color name %s", name);
		return (~meta.vanillaId) & 0xF;
	}

	private static String colorToName(int color) {
		ColorMeta meta = ColorUtils.vanillaToColor((~color) & 0xF);
		Preconditions.checkNotNull(meta, "Invalid color id %s", color);
		return meta.name;
	}
}
