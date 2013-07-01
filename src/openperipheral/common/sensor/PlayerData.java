package openperipheral.common.sensor;

import java.util.Collection;
import java.util.HashMap;

import openperipheral.common.util.InventoryUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class PlayerData extends HashMap implements IEntityData {

	private HashMap position = new HashMap();
	private HashMap potionEffects = new HashMap();
	private HashMap armour = new HashMap();
	private HashMap lookingAt = new HashMap();
	private HashMap inventory = new HashMap();
	
	public PlayerData() {
		armour.put("boots", new HashMap());
		armour.put("leggings", new HashMap());
		armour.put("chestplate", new HashMap());
		armour.put("helmet", new HashMap());
		put("armour", armour);
		put("position", position);
		put("potionEffects", potionEffects);
		put("slots", inventory);
	}
	
	public void fromPlayer(Vec3 sensorPosition, EntityPlayer player) {
		
		potionEffects.clear();
		
		position.put("x", player.posX - sensorPosition.xCoord);
		position.put("y", player.posY - sensorPosition.yCoord);
		position.put("z", player.posZ - sensorPosition.zCoord);
		put("username", player.username);
		InventoryUtils.itemstackToMap((HashMap)armour.get("boots"), player.getCurrentItemOrArmor(1));
		InventoryUtils.itemstackToMap((HashMap)armour.get("leggings"), player.getCurrentItemOrArmor(2));
		InventoryUtils.itemstackToMap((HashMap)armour.get("chestplate"), player.getCurrentItemOrArmor(3));
		InventoryUtils.itemstackToMap((HashMap)armour.get("helmet"), player.getCurrentItemOrArmor(4));
		put("health", player.getHealth());
		put("isAirborne", player.isAirBorne);
		put("isJumping", player.isJumping);
		put("isBlocking", player.isBlocking());
		put("isBurning", player.isBurning());
		put("isAlive", player.isEntityAlive());
		put("isInWater", player.isInWater());
		put("isOnLadder", player.isOnLadder());
		put("isSleeping", player.isPlayerSleeping());
		put("isRiding", player.isRiding());
		put("isSneaking", player.isSneaking());
		put("isSprinting", player.isSprinting());
		put("isWet", player.isWet());
		put("isChild", player.isChild());
		put("isHome", player.isWithinHomeDistanceCurrentPosition());
		put("yaw", player.rotationYaw);
		put("pitch", player.rotationPitch);
		put("yawHead", player.rotationYawHead);
		Collection<PotionEffect> effects = player.getActivePotionEffects();
		int count = 1;
		potionEffects.clear();
		for (PotionEffect effect : effects) {
			potionEffects.put(count, effect.getEffectName());
			count++;
		}
		put("foodLevel", player.getFoodStats().getFoodLevel());
		put("isCreativeMode", player.capabilities.isCreativeMode);
		InventoryUtils.invToMap(inventory, player.inventory);
        Vec3 posVec = player.worldObj.getWorldVec3Pool().getVecFromPool(player.posX, player.posY + 1.62F, player.posZ);
        Vec3 lookVec = player.getLook(1.0f);
        Vec3 targetVec = posVec.addVector(lookVec.xCoord * 10f, lookVec.yCoord * 10f, lookVec.zCoord * 10f);
        MovingObjectPosition mop = player.worldObj.rayTraceBlocks(posVec, targetVec);
    	put("IsLookingAtBlock", false);
        if (mop != null) {
        	put("IsLookingAtBlock", mop.typeOfHit == EnumMovingObjectType.TILE);
            if (mop.typeOfHit == EnumMovingObjectType.TILE) {
            	int blockId = player.worldObj.getBlockId(mop.blockX, mop.blockY, mop.blockZ);
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
