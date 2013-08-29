package openperipheral.core.adapter.enderstorage;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import openperipheral.api.Arg;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.core.util.CallWrapper;
import openperipheral.core.util.ReflectionHelper;
import dan200.computer.api.IComputerAccess;

public class AdapterFrequencyOwner implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return ReflectionHelper.getClass("codechicken.enderstorage.common.TileFrequencyOwner");
	}

	@LuaMethod(
			returnType = LuaType.TABLE,
			description = "Get the colours active on this chest or tank")
	public HashMap getColors(IComputerAccess computer, TileEntity frequencyOwner) {
		HashMap map = new HashMap();
		int frequency = getFreq(frequencyOwner);
		map.put(1, (frequency >> 8 & 0xF));
		map.put(2, (frequency >> 4 & 0xF));
		map.put(3, (frequency & 0xF));
		return map;
	}

	@LuaMethod(
			returnType = LuaType.VOID,
			description = "Set the colors of this chest or tank",
			args = { @Arg(
					type = LuaType.NUMBER,
					name = "one",
					description = "Color one"), @Arg(
					type = LuaType.NUMBER,
					name = "one",
					description = "Color two"), @Arg(
					type = LuaType.NUMBER,
					name = "one",
					description = "Color three"), })
	public Void setColors(IComputerAccess computer, TileEntity frequencyOwner, int one, int two, int three) {
		int frequency = ((one & 0xF) << 8) + ((two & 0xF) << 4) + (three & 0xF);
		return new CallWrapper<Void>().call(frequencyOwner, "setFreq", frequency);
	}

	@LuaMethod(
			returnType = LuaType.TABLE,
			description = "Get the frequency of this chest or tank")
	public int getFrequency(IComputerAccess computer, TileEntity frequencyOwner) {
		return getFreq(frequencyOwner);
	}

	@LuaMethod(
			returnType = LuaType.VOID,
			description = "Set the frequency of this chest or tank",
			args = { @Arg(
					type = LuaType.NUMBER,
					name = "frequency",
					description = "The frequency"), })
	public Void setFrequency(IComputerAccess computer, TileEntity frequencyOwner, int frequency) {
		return new CallWrapper<Void>().call(frequencyOwner, "setFreq", frequency);
	}

	private int getFreq(TileEntity frequencyOwner) {
		NBTTagCompound nbt = new NBTTagCompound();
		frequencyOwner.writeToNBT(nbt);
		if (nbt.hasKey("freq")) { return nbt.getInteger("freq"); }
		return 0;
	}

}
