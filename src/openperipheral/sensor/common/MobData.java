package openperipheral.sensor.common;

import java.util.Collection;
import java.util.HashMap;

import openperipheral.core.util.InventoryUtils;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class MobData extends HashMap implements IEntityData {


	private HashMap position = new HashMap();
	private HashMap potionEffects = new HashMap();
	private HashMap armour = new HashMap();
	private HashMap lookingAt = new HashMap();
	
	public MobData() {
		armour.put("boots", new HashMap());
		armour.put("leggings", new HashMap());
		armour.put("chestplate", new HashMap());
		armour.put("helmet", new HashMap());
		put("armour", armour);
		put("position", position);
		put("potionEffects", potionEffects);
	}
	
	public void fromEntity(Vec3 sensorPosition, EntityLiving living) {

		potionEffects.clear();
		
		position.put("x", living.posX - sensorPosition.xCoord);
		position.put("y", living.posY + living.getEyeHeight() - sensorPosition.yCoord);
		position.put("z", living.posZ - sensorPosition.zCoord);
		InventoryUtils.itemstackToMap((HashMap)armour.get("boots"), living.getCurrentItemOrArmor(1));
		InventoryUtils.itemstackToMap((HashMap)armour.get("leggings"), living.getCurrentItemOrArmor(2));
		InventoryUtils.itemstackToMap((HashMap)armour.get("chestplate"), living.getCurrentItemOrArmor(3));
		InventoryUtils.itemstackToMap((HashMap)armour.get("helmet"), living.getCurrentItemOrArmor(4));
		put("health", living.getHealth());
		put("isAirborne", living.isAirBorne);
		put("isJumping", living.isJumping);
		put("isBlocking", living.isBlocking());
		put("isBurning", living.isBurning());
		put("isAlive", living.isEntityAlive());
		put("isInWater", living.isInWater());
		put("isOnLadder", living.isOnLadder());
		put("isSleeping", living.isPlayerSleeping());
		put("isRiding", living.isRiding());
		put("isSneaking", living.isSneaking());
		put("isSprinting", living.isSprinting());
		put("isWet", living.isWet());
		put("isChild", living.isChild());
		put("isDead", living.isDead);
		put("isHome", living.isWithinHomeDistanceCurrentPosition());
		put("yaw", living.rotationYaw);
		put("pitch", living.rotationPitch);
		put("yawHead", living.rotationYawHead);
		Collection<PotionEffect> effects = living.getActivePotionEffects();
		int count = 1;
		potionEffects.clear();
		for (PotionEffect effect : effects) {
			potionEffects.put(count, effect.getEffectName());
			count++;
		}
        Vec3 posVec = living.worldObj.getWorldVec3Pool().getVecFromPool(living.posX, living.posY + 1.62F, living.posZ);
        Vec3 lookVec = living.getLook(1.0f);
        Vec3 targetVec = posVec.addVector(lookVec.xCoord * 10f, lookVec.yCoord * 10f, lookVec.zCoord * 10f);
        MovingObjectPosition mop = living.worldObj.rayTraceBlocks(posVec, targetVec);
    	put("IsLookingAtBlock", false);
        if (mop != null) {
        	put("IsLookingAtBlock", mop.typeOfHit == EnumMovingObjectType.TILE);
            if (mop.typeOfHit == EnumMovingObjectType.TILE) {
            	int blockId = living.worldObj.getBlockId(mop.blockX, mop.blockY, mop.blockZ);
            	HashMap lookingAt = new HashMap();
            	lookingAt.put("x", mop.blockX - sensorPosition.xCoord);
            	lookingAt.put("y", mop.blockY - sensorPosition.yCoord);
            	lookingAt.put("z", mop.blockZ - sensorPosition.zCoord);
            	put("lookingAt", lookingAt);
            }else {
            	put("lookingAt", null);
            }	
        }
        

	}

}
