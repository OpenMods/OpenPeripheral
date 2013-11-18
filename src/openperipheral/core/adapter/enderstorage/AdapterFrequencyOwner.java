package openperipheral.core.adapter.enderstorage;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.core.util.ReflectionHelper;
import dan200.computer.api.IComputerAccess;

public class AdapterFrequencyOwner implements IPeripheralAdapter {
	private static final Class<?> CLAZZ = ReflectionHelper.getClass("codechicken.enderstorage.common.TileFrequencyOwner");
	
	@Override
	public Class<?> getTargetClass() {
		return CLAZZ;
	}

	@LuaMethod(
		returnType = LuaType.TABLE, onTick = false, description = "Get the colours active on this chest or tank")
	public HashMap<Integer, Double> getColors(IComputerAccess computer, TileEntity frequencyOwner) {
		// get the current frequency
		int frequency = getFreq(frequencyOwner);
		// return a map of the frequency in ComputerCraft colour format
		HashMap<Integer, Double> colors = new HashMap<Integer, Double>(3);
		// convert to ComputerCraft color format by applying 2^n
		colors.put(1, Math.pow(2, (frequency >> 8 & 0xF)));
		colors.put(2, Math.pow(2, (frequency >> 4 & 0xF)));
		colors.put(3, Math.pow(2, (frequency & 0xF)));
		return colors;
	}
	
	@LuaMethod(
		returnType = LuaType.VOID, onTick = false, description = "Set the frequency of this chest or tank",
			args = {
			@Arg(name = "color_left", type = LuaType.NUMBER, description = "The first color"),
			@Arg(name = "color_middle", type = LuaType.NUMBER, description = "The second color"),
			@Arg(name = "color_right", type = LuaType.NUMBER, description = "The third color")})
	public void setColors(IComputerAccess computer, TileEntity frequencyOwner, int color_left, int color_middle, int color_right) throws Exception {
		// transform the ComputerCraft colours (2^n) into the range 0-15 And validate they're within this range
		int high = parseComputerCraftColor(color_left);
		int med = parseComputerCraftColor(color_middle);
		int low = parseComputerCraftColor(color_right);
		// convert the three colours into a single colour
		int frequency = ((high & 0xF) << 8) + ((med & 0xF) << 4) + (low & 0xF);
		// set the TE's frequency to the new colours
		setFreq(frequencyOwner, frequency);
	}
	
	@LuaMethod(
		returnType = LuaType.TABLE, onTick = false, description = "Get the frequency of this chest or tank")
	public int getFrequency(IComputerAccess computer, TileEntity frequencyOwner) {
		return getFreq(frequencyOwner);
	}
	
	@LuaMethod(
		returnType = LuaType.VOID, onTick = false, description = "Set the frequency of this chest or tank",
			args = {
			@Arg(name = "frequency", type = LuaType.NUMBER, description = "A single color that represents all three colours on this chest or tank") })
	public void setFrequency(IComputerAccess computer, TileEntity frequencyOwner, int frequency) throws Exception {
		setFreq(frequencyOwner, frequency);
	}

	private int getFreq(TileEntity frequencyOwner) {
		NBTTagCompound nbt = new NBTTagCompound();
		frequencyOwner.writeToNBT(nbt);
		if (nbt.hasKey("freq")) { return nbt.getInteger("freq"); }
		return 0;
	}
	
	private void setFreq(TileEntity frequencyOwner, int frequency) throws Exception {
		if (frequency < 0 || frequency > 4095) { throw new Exception("Frequency out of bounds. Should be 0-4095"); }
		ReflectionHelper.callMethod(getTargetClass(), frequencyOwner, new String[] {"setFreq"}, frequency);
	}
	
	private static int parseComputerCraftColor(int color) throws Exception {
		if (color < 0 || color > 32768) { throw new Exception("Invalid color supplied"); }
		double val = Math.log(color)/Math.log(2);
		if (val < 0 || val > 15 || val % 1 != 0) { throw new Exception("Invalid color supplied."); }
		return (int)val;
	}
}
