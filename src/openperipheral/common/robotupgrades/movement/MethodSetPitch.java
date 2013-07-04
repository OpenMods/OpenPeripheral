package openperipheral.common.robotupgrades.movement;

import java.util.ArrayList;

import net.minecraft.entity.EntityCreature;
import openperipheral.api.IRestriction;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.common.restriction.RestrictionMaximum;
import openperipheral.common.restriction.RestrictionMinimum;

public class MethodSetPitch implements IRobotMethod {

	ArrayList<IRestriction> restrictions;
	
	public MethodSetPitch() {
		restrictions = new ArrayList<IRestriction>();
		restrictions.add(new RestrictionMaximum(45));
		restrictions.add(new RestrictionMinimum(-45));
	}
	
	@Override
	public boolean needsSanitize() {
		return true;
	}

	@Override
	public ArrayList<IRestriction> getRestrictions(int index) {
		return restrictions;
	}

	@Override
	public String getLuaName() {
		return "setPitch";
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public Class[] getRequiredParameters() {
		return new Class[] { float.class };
	}

	@Override
	public Object execute(IRobotUpgradeInstance instance, Object[] args) throws Exception {
		IRobot robot = ((InstanceMovementUpgrade)instance).getRobot();
		EntityCreature creature = robot.getEntity();
		float direction = (Float)args[0];
		robot.setPitch(direction);
		return true;
	}

}
