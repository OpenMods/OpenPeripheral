package openperipheral.common.robotupgrades.lazers;

import java.util.ArrayList;

import net.minecraft.entity.EntityCreature;
import openperipheral.api.IRestriction;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.common.entity.EntityLazer;

public class MethodLazerFire implements IRobotMethod {

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
		return "fire";
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
		EntityCreature entity = ((InstanceLazersUpgrade)instance).getEntity();
		entity.playSound("openperipheral.lazer", 1F, entity.worldObj.rand.nextFloat() + 0.4f);
		EntityLazer lazer = new EntityLazer(entity.worldObj, entity);
		entity.worldObj.spawnEntityInWorld(lazer);
		System.out.println("HEre3");
		return true;
	}

}
