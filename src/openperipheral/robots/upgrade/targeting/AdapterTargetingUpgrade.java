package openperipheral.robots.upgrade.targeting;

import java.util.HashMap;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import openperipheral.api.Arg;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotUpgradeAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import openperipheral.codechicken.core.vec.Rotation;
import openperipheral.codechicken.core.vec.Vector3;
import dan200.computer.api.IComputerAccess;

public class AdapterTargetingUpgrade implements IRobotUpgradeAdapter {

	private IRobot robot;
	private int tier;

	public AdapterTargetingUpgrade(IRobot robot, int tier) {
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
		pos.yCoord += robot.getRobotEyeHeight();
		return pos;
	}

	@LuaMethod(
			args = { @Arg(
					type = LuaType.NUMBER,
					name = "x"), @Arg(
					type = LuaType.NUMBER,
					name = "y"), @Arg(
					type = LuaType.NUMBER,
					name = "z") })
	public void lookAt(IComputerAccess computer, IRobot robot, double x, double y, double z) {
		aimAtFromVec(getEyePosition(), x, y, z);
	}

	@LuaMethod(
			args = { @Arg(
					type = LuaType.NUMBER,
					name = "x"), @Arg(
					type = LuaType.NUMBER,
					name = "y"), @Arg(
					type = LuaType.NUMBER,
					name = "z") })
	public void aimAt(IComputerAccess computer, IRobot robot, double x, double y, double z) throws Exception {
		if (tier < 2) { throw new Exception("A higher tier upgrade is required"); }
		double radYaw = -Math.toRadians(robot.getYaw());

		Vector3 pos = new Vector3(-20 / 16D, 0, 0).apply(new Rotation(radYaw, 0, 1, 0)).add(new Vector3(getEyePosition()));

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
