package openperipheral.api;

import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IRobotUpgradeInstance {
	public void writeToNBT(NBTTagCompound nbt);
	public void readFromNBT(NBTTagCompound nbt);
	public HashMap<Integer, EntityAIBase> getAITasks();
	public void update();
}
