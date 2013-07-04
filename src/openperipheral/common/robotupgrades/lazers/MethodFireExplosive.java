package openperipheral.common.robotupgrades.lazers;

import java.util.ArrayList;

import net.minecraft.entity.EntityCreature;

import openperipheral.api.IRestriction;
import openperipheral.api.IRobotMethod;
import openperipheral.api.IRobotUpgradeInstance;
import openperipheral.common.entity.EntityLazer;
import openperipheral.common.item.meta.MetaEnergyCell;

public class MethodFireExplosive extends MethodFireLazer implements IRobotMethod {

	@Override
	public String getLuaName() {
		return "fireExplosive";
	}
	
	@Override
	public double getHeatModifier() {
		return 4.0;
	}
	
	@Override
	public Class getAmmoClass() {
		return MetaEnergyCell.class;
	}

	@Override
	protected EntityLazer createLazer(EntityCreature entity) {
		EntityLazer lazer = new EntityLazer(entity.worldObj, entity);
		lazer.setExplosive(true);
		return lazer;
	}

}
