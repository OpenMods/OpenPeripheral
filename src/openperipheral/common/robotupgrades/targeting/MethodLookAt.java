package openperipheral.common.robotupgrades.targeting;

import java.util.ArrayList;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import openperipheral.api.IRestriction;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.codechicken.core.vec.Rotation;
import openperipheral.codechicken.core.vec.Vector3;

public class MethodLookAt implements IRobotMethod {

	public MethodLookAt() {
	}

	@Override
	public boolean needsSanitize() {
		return true;
	}

	@Override
	public ArrayList<IRestriction> getRestrictions(int index) {
		return null;
	}

	@Override
	public String getLuaName() {
		return "lookAt";
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public Class[] getRequiredParameters() {
		return new Class[] { double.class, double.class, double.class };
	}

	@Override
	public Object execute(IRobotUpgradeInstance instance, Object[] args) throws Exception {
		InstanceTargetingUpgrade upgrade = (InstanceTargetingUpgrade) instance;
		IRobot robot = upgrade.getRobot();
		EntityCreature entity = robot.getEntity();
		
		double targetX = (Double) args[0];
		double targetY = (Double) args[1];
		double targetZ = (Double) args[2];

		Vec3 startPosition = getStartPosition(robot);
		
		double distanceX = targetX - startPosition.xCoord;
		double distanceY = targetY - startPosition.yCoord;
		double distanceZ = targetZ - startPosition.zCoord;
        
        double d3 = (double)MathHelper.sqrt_double(distanceX * distanceX + distanceZ * distanceZ);
        float f2 = (float)(Math.atan2(distanceZ, distanceX) * 180.0D / Math.PI) - 90.0F;
        float f3 = (float)(-(Math.atan2(distanceY, d3) * 180.0D / Math.PI));
        
        robot.setYaw(f2);
        robot.setPitch(f3);
		
		return true;
	}

	protected Vec3 getStartPosition(IRobot robot) {
		Vec3 pos = robot.getLocation();
		pos.yCoord += robot.getEyeHeight();
		return pos;
	}

}
