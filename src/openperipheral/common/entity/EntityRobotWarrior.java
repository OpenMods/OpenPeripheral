package openperipheral.common.entity;

import openperipheral.api.EnumRobotType;
import openperipheral.api.ILazerRobot;
import openperipheral.common.util.ReflectionHelper;
import net.minecraft.entity.WatchableObject;
import net.minecraft.world.World;

public class EntityRobotWarrior extends EntityRobot implements ILazerRobot {
	
	/**
	 * The weapon spin used for rendering clientside
	 */
	private float weaponSpin = 0.f;

	private float weaponSpinSpeed = 0;
	
	private String[] dataWatcherMethod = new String[] { "getWatchedObject" };
	
	public EntityRobotWarrior(World world) {
		super(world);
	}

	@Override
	public void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(11, 0.0F);
	}
	
	@Override
	public void setWeaponSpinSpeed(float speed) {
		weaponSpinSpeed = speed;
        this.dataWatcher.updateObject(11, weaponSpinSpeed);
	}

	@Override
	public void modifyWeaponSpinSpeed(float speed) {
		weaponSpinSpeed += speed;
		weaponSpinSpeed = Math.max(0, weaponSpinSpeed);
        this.dataWatcher.updateObject(11, weaponSpinSpeed);
	}
	
	@Override
	public float getWeaponSpinSpeed() {
		WatchableObject object = (WatchableObject)ReflectionHelper.callMethod("", this.dataWatcher, dataWatcherMethod, 11);
		return (Float)(object.getObject());
	}
	
	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		if (worldObj.isRemote) { 
			this.weaponSpin += getWeaponSpinSpeed();
		}
	}

	public float getWeaponSpin() {
		return weaponSpin;
	}
	
	@Override
	public EnumRobotType getRobotType() {
		return EnumRobotType.Warrior;
	}

}
