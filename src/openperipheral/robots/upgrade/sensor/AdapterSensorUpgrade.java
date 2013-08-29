package openperipheral.robots.upgrade.sensor;

import java.util.HashMap;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeAdapter;
import openperipheral.core.adapter.AdapterSensor;

public class AdapterSensorUpgrade extends AdapterSensor implements IRobotUpgradeAdapter {

	private IRobot robot;
	private int tier;

	public AdapterSensorUpgrade(IRobot robot, int tier) {
		this.robot = robot;
		this.tier = tier;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {}

	@Override
	public HashMap<Integer, EntityAIBase> getAITasks() {
		return null;
	}

	@Override
	public void update() {}

	@Override
	public void onTierChanged(int tier) {
		this.tier = tier;
	}
}
