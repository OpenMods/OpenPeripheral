package openperipheral.common.robotupgrades.targeting;

import java.util.ArrayList;

import net.minecraft.util.Vec3;

import openperipheral.api.IRestriction;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.codechicken.core.vec.Rotation;
import openperipheral.codechicken.core.vec.Vector3;

public class MethodAimAt extends MethodLookAt implements IRobotMethod {

	@Override
	public String getLuaName() {
		return "aimAt";
	}
	
	@Override
	protected Vec3 getStartPosition(IRobot robot) {

		double radYaw = -Math.toRadians(robot.getYaw());
		
		Vector3 pos = new Vector3(-20/16D, 0, 0)
						.apply(new Rotation(radYaw, 0, 1, 0))
						.add(new Vector3(super.getStartPosition(robot)));
		
		return pos.toVec3D();
	}
	
}
