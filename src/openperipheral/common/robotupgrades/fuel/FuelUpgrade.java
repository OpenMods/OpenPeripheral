package openperipheral.common.robotupgrades.fuel;

import java.util.HashMap;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;

public class FuelUpgrade implements IRobotUpgradeInstance {

	private IRobot robot;
	
	public FuelUpgrade(IRobot robot) {
		this.robot = robot;
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
	public void update() {
	}

}
