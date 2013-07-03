package openperipheral.common.robotupgrades.movement;

import java.util.HashMap;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;

public class InstanceMovementUpgrade implements IRobotUpgradeInstance {

	private double targetX;
	private double targetY;
	private double targetZ;
	private boolean shouldMoveToTarget = false;
	private IRobot robot;
	
	public InstanceMovementUpgrade(IRobot robot) {
		this.robot = robot;
	}
	
	public IRobot getRobot() {
		return robot;
	}
	
	public boolean shouldMoveToTarget() {
		return shouldMoveToTarget;
	}
	
	public void setShouldMoveToTarget(boolean should) {
		shouldMoveToTarget = should;
	}
	
	public double getTargetLocationX() {
		return targetX;
	}
	
	public double getTargetLocationY() {
		return targetY;
	}
	
	public double getTargetLocationZ() {
		return targetZ;
	}
	
	public void setTargetLocation(double x, double y, double z) {
		targetX = x;
		targetY = y;
		targetZ = z;
		shouldMoveToTarget = true;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
	}

	@Override
	public HashMap<Integer, EntityAIBase> getAITasks() {
		HashMap<Integer, EntityAIBase> tasks = new HashMap<Integer, EntityAIBase>();
		tasks.put(0,  new EntityAIGotoLocation(this, robot));
		return tasks;
	}

	@Override
	public void update() {
		
	}

}
