package openperipheral.core.adapter.enderstorage;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.core.util.ReflectionHelper;
import dan200.computer.api.IComputerAccess;

public class AdapterFrequencyOwner implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return ReflectionHelper.getClass("codechicken.enderstorage.common.TileFrequencyOwner");
	}

	@LuaMethod(
		returnType = LuaType.TABLE, onTick = false, description = "Get the colours active on this chest or tank")
	public HashMap<Integer, Double> getColors(IComputerAccess computer, TileEntity frequencyOwner) {
		HashMap<Integer, Double> map = new HashMap<Integer, Double>();
		int frequency = getFreq(frequencyOwner);
		map.put(1, Math.pow(2, (frequency >> 8 & 0xF)));
		map.put(2, Math.pow(2, (frequency >> 4 & 0xF)));
		map.put(3, Math.pow(2, (frequency & 0xF)));
		return map;
	}

	@LuaMethod(
		returnType = LuaType.TABLE, onTick = false, description = "Get the frequency of this chest or tank")
	public int getFrequency(IComputerAccess computer, TileEntity frequencyOwner) {
		return getFreq(frequencyOwner);
	}

	private int getFreq(TileEntity frequencyOwner) {
		NBTTagCompound nbt = new NBTTagCompound();
		frequencyOwner.writeToNBT(nbt);
		if (nbt.hasKey("freq")) { return nbt.getInteger("freq"); }
		return 0;
	}

}
