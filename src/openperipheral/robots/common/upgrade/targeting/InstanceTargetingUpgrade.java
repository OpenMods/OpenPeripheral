package openperipheral.robots.common.upgrade.targeting;

import java.util.HashMap;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.api.LuaMethod;
import openperipheral.codechicken.core.vec.Rotation;
import openperipheral.codechicken.core.vec.Vector3;

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
	
	public Vec3 getEyePosition() {
		Vec3 pos = robot.getLocation();
		pos.yCoord += robot.getEyeHeight();
		return pos;
	}
	
	@LuaMethod
	public void lookAt(double x, double y, double z) {
		aimAtFromVec(getEyePosition(), x, y, z);
	}
	
	@LuaMethod
	public void aimAt(double x, double y, double z) {

		double radYaw = -Math.toRadians(robot.getYaw());
		
		Vector3 pos = new Vector3(-20/16D, 0, 0)
						.apply(new Rotation(radYaw, 0, 1, 0))
						.add(new Vector3(getEyePosition()));
		
		aimAtFromVec(pos.toVec3D(), x, y, z);
	}
	
	public void aimAtFromVec(Vec3 startPosition, double targetX, double targetY, double targetZ) {
		
		double distanceX = targetX - startPosition.xCoord;
		double distanceY = targetY - startPosition.yCoord;
		double distanceZ = targetZ - startPosition.zCoord;
        
        double d3 = (double)MathHelper.sqrt_double(distanceX * distanceX + distanceZ * distanceZ);
        float f2 = (float)(Math.atan2(distanceZ, distanceX) * 180.0D / Math.PI) - 90.0F;
        float f3 = (float)(-(Math.atan2(distanceY, d3) * 180.0D / Math.PI));
        
        robot.setYaw(f2);
        robot.setPitch(f3);
	}

}
