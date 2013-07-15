package openperipheral.robots.upgrade.sensor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dan200.computer.api.IComputerAccess;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.core.adapter.AdapterSensor;
import openperipheral.core.interfaces.ISensorEnvironment;

public class AdapterSensorUpgrade extends AdapterSensor implements IRobotUpgradeAdapter {

	private IRobot robot;
	private int tier;
	
	public AdapterSensorUpgrade(IRobot robot, int tier) {
		this.robot = robot;
		this.tier = tier;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
	}

	@Override
	public HashMap<Integer, EntityAIBase> getAITasks() {
		return null;
	}

	@Override
	public void update() {
	}

	@Override
	public void onTierChanged(int tier) {
		this.tier = tier;
	}
}
