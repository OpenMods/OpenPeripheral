package openperipheral.robots.common.upgrade.movement;

import java.util.HashMap;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeHooks;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.LuaMethod;

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

	@LuaMethod(name="goto")
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

	@Override
	public void onTierChanged(int tier) {
		// TODO Auto-generated method stub
		
	}
	
	@LuaMethod
	public Object[] getLocation() {
		Vec3 loc = robot.getLocation();
		return new Object[] { loc.xCoord, loc.yCoord, loc.zCoord };
	}
	
	public void jump() {
		EntityCreature creature = robot.getEntity();
		creature.motionY = 0.41999998688697815D;

		creature.motionY *= 1;

		if (creature.isSprinting()) {
			float f = creature.rotationYaw * 0.017453292F;
			creature.motionX -= (double) (MathHelper.sin(f) * 0.2F);
			creature.motionZ += (double) (MathHelper.cos(f) * 0.2F);
		}

		creature.isAirBorne = true;
		ForgeHooks.onLivingJump(creature);
		creature.playSound("openperipheral.robotjump", 1F, 1F);
	}
	
	@LuaMethod
	public void setPitch(float pitch) {
		robot.setPitch(pitch);
	}
	
	@LuaMethod
	public float getPitch() {
		return robot.getPitch();
	}
	
	@LuaMethod
	public void setYaw(float yaw) {
		robot.setYaw(yaw);
	}
	
	@LuaMethod
	public float getYaw() {
		return robot.getYaw();
	}
}
