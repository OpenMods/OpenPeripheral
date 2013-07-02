package openperipheral.common.robotupgrades.movement;

import java.util.ArrayList;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeHooks;

import openperipheral.api.IRestriction;
import openperipheral.api.IRobot;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;

public class MethodJump implements IRobotMethod {

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
		return "jump";
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public Class[] getRequiredParameters() {
		return null;
	}

	@Override
	public Object execute(IRobotUpgradeInstance instance, Object[] args) throws Exception {
		IRobot robot = ((MovementUpgrade)instance).getRobot();
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
		return true;
	}

}
