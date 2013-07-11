package openperipheral.robots.upgrade.movement;

import java.util.HashMap;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeHooks;
import openperipheral.api.IMultiReturn;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.LuaMethod;

public class InstanceMovementUpgrade implements IRobotUpgradeInstance {

	private double targetX;
	private double targetY;
	private double targetZ;
	private boolean shouldMoveToTarget = false;
	private IRobot robot;
	private int tier;
	
	public InstanceMovementUpgrade(IRobot robot, int tier) {
		this.robot = robot;
		this.tier = tier;
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
		this.tier = tier;
	}
	
	@LuaMethod
	public IMultiReturn getLocation() throws Exception {
		if (tier < 3) {
			throw new Exception("At least a tier 3 movement upgrade required");
		}
		final Vec3 loc = robot.getLocation();
		return new IMultiReturn() {
			@Override
			public Object[] getObjects() {
				return new Object[] {
					loc.xCoord,
					loc.yCoord,
					loc.zCoord
				};
			}
		};
	}
	
	@LuaMethod
	public void jump() throws Exception {
		if (tier < 2) {
			throw new Exception("At least a tier 2 movement upgrade required");
		}
		EntityCreature creature = robot.getEntity();
		if (!robot.isJumping()) {
			creature.getJumpHelper().setJumping();
			ForgeHooks.onLivingJump(creature);
			creature.playSound("openperipheral:robotjump", 1F, 1F);
		}
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
