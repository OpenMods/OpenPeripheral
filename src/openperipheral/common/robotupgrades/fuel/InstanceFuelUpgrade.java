package openperipheral.common.robotupgrades.fuel;

import java.util.HashMap;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;

public class InstanceFuelUpgrade implements IRobotUpgradeInstance {

	private IRobot robot;
	
	public InstanceFuelUpgrade(IRobot robot) {
		this.robot = robot;
	}
	
	public IRobot getRobot() {
		return robot;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setFloat("fuelLevel", robot.getFuelLevel());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		robot.setFuelLevel(nbt.getFloat("fuelLevel"));
	}

	@Override
	public HashMap<Integer, EntityAIBase> getAITasks() {
		return null;
	}

	@Override
	public void update() {
	}

}
