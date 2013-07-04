package openperipheral.common.robotupgrades.targeting;

import java.util.HashMap;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;

public class InstanceTargetingUpgrade implements IRobotUpgradeInstance {

	private IRobot robot;
	private int tier;
	
	public InstanceTargetingUpgrade(IRobot robot, int tier) {
		this.robot = robot;
		this.tier = tier;
	}

	public IRobot getRobot() {
		return robot;
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
	public void onTierChanged(int tier) {

	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

}
