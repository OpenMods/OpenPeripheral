package openperipheral.core.integration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import openperipheral.core.AdapterManager;
import openperipheral.core.adapter.vanilla.AdapterBeacon;
import openperipheral.core.adapter.vanilla.AdapterBrewingStand;
import openperipheral.core.adapter.vanilla.AdapterComparator;
import openperipheral.core.adapter.vanilla.AdapterFurnace;
import openperipheral.core.adapter.vanilla.AdapterInventory;
import openperipheral.core.adapter.vanilla.AdapterMobSpawner;
import openperipheral.core.adapter.vanilla.AdapterNoteBlock;
import openperipheral.core.adapter.vanilla.AdapterRecordPlayer;
import openperipheral.core.util.InventoryUtils;
import openperipheral.core.util.ReflectionHelper;

public class ModuleVanilla {
	public static void init() {
		AdapterManager.addPeripheralAdapter(new AdapterInventory());
		AdapterManager.addPeripheralAdapter(new AdapterNoteBlock());
		AdapterManager.addPeripheralAdapter(new AdapterComparator());
		AdapterManager.addPeripheralAdapter(new AdapterBrewingStand());
		AdapterManager.addPeripheralAdapter(new AdapterFurnace());
		AdapterManager.addPeripheralAdapter(new AdapterRecordPlayer());
		AdapterManager.addPeripheralAdapter(new AdapterBeacon());
		AdapterManager.addPeripheralAdapter(new AdapterMobSpawner());
	}
	
	public static void entityToMap(Entity entity, HashMap map, Vec3 relativePos) {
		if (entity instanceof IInventory) {
			map.put("inventory", InventoryUtils.invToMap((IInventory)entity));
		}

		if (entity instanceof EntityHorse) {
			EntityHorse horse = (EntityHorse)entity;
			IInventory invent = (IInventory)ReflectionHelper.getProperty("", horse, "field_110296_bG");

			map.put("eatingHaystack", horse.isEatingHaystack());
			map.put("chestedHorse", horse.isChested());
			map.put("hasReproduced", horse.getHasReproduced());
			map.put("bred", horse.func_110205_ce());
			map.put("horseType", horse.getHorseType());
			map.put("horseVariant", horse.getHorseVariant());
			map.put("horseTemper", horse.getTemper());
			map.put("horseTame", horse.isTame());
			map.put("ownerName", horse.getOwnerName());
			map.put("horseInventory", InventoryUtils.invToMap(invent));
		}
		
		if (entity instanceof EntityVillager) {
			EntityVillager villager = (EntityVillager) entity;
			map.put("profession", villager.getProfession());
			map.put("isMating", villager.isMating());
			map.put("isPlaying", villager.isPlaying());
			boolean isTrading = villager.isTrading();
			map.put("isTrading", isTrading);
			if (isTrading) {
				map.put("customer", villager.getCustomer().username);
			}
		}

		if (entity instanceof EntitySheep) {
			EntitySheep sheep = (EntitySheep)entity;
			map.put("sheepColor", sheep.getFleeceColor());
			map.put("isSheared", sheep.getSheared());
		}

		if (entity instanceof EntityZombie) {
			EntityZombie zombie = (EntityZombie)entity;
			map.put("isVillagerZombie", zombie.isVillager());
			map.put("convertingToVillager", zombie.isConverting());
		}
		
		if (entity instanceof EntityBat) {
			EntityBat bat = (EntityBat)entity;
			map.put("isHanging", bat.getIsBatHanging());
		}
		
		if (entity instanceof EntityPig) {
			EntityPig pig = (EntityPig)entity;
			map.put("isSaddled", pig.getSaddled());
		}
		
		if (entity instanceof EntityWolf) {
			EntityWolf wolf = (EntityWolf)entity;
			map.put("isShaking", wolf.getWolfShaking());
			map.put("isAngry", wolf.isAngry());
			map.put("collarColor", Math.pow(2, wolf.getCollarColor()));
		}
		
		if (entity instanceof EntityTameable) {
			EntityTameable mob = (EntityTameable)entity;
			boolean isTamed = mob.isTamed();
			map.put("isTamed", isTamed);
			if (isTamed) {
				map.put("isSitting", mob.isSitting());
				map.put("owner", mob.getOwnerName());
			}
		}
		
		if (entity instanceof EntityCreeper) {
			EntityCreeper creeper = (EntityCreeper)entity;
			map.put("isCharged", creeper.getPowered());
		}
		
		if (entity instanceof EntityWitch) {
			EntityWitch witch = (EntityWitch)entity;
			map.put("isAggressive", witch.getAggressive());
		}

		if (entity instanceof EntityLivingBase) {

			EntityLivingBase living = (EntityLivingBase)entity;

			HashMap armor = new HashMap();
			map.put("armor", armor);
			HashMap potionEffects = new HashMap();
			map.put("potionEffects", potionEffects);

			armor.put("boots", InventoryUtils.itemstackToMap(living.getCurrentItemOrArmor(1)));
			armor.put("leggings", InventoryUtils.itemstackToMap(living.getCurrentItemOrArmor(2)));
			armor.put("chestplate", InventoryUtils.itemstackToMap(living.getCurrentItemOrArmor(3)));
			armor.put("helmet", InventoryUtils.itemstackToMap(living.getCurrentItemOrArmor(4)));
			map.put("health", living.getHealth());
			map.put("maxHealth", living.getMaxHealth());
			map.put("isAirborne", living.isAirBorne);
			map.put("isBurning", living.isBurning());
			map.put("isAlive", living.isEntityAlive());
			map.put("isInWater", living.isInWater());
			map.put("isOnLadder", living.isOnLadder());
			map.put("isSleeping", living.isPlayerSleeping());
			map.put("isRiding", living.isRiding());
			map.put("isSneaking", living.isSneaking());
			map.put("isSprinting", living.isSprinting());
			map.put("isWet", living.isWet());
			map.put("isChild", living.isChild());
			map.put("isDead", living.isDead);
			map.put("yaw", living.rotationYaw);
			map.put("pitch", living.rotationPitch);
			map.put("yawHead", living.rotationYawHead);
			map.put("heldItem", InventoryUtils.itemstackToMap(living.getHeldItem()));

			Collection<PotionEffect> effects = living.getActivePotionEffects();

			int count = 1;
			for (PotionEffect effect : effects) {
				potionEffects.put(count++, effect.getEffectName());
			}

			Vec3 posVec = living.worldObj.getWorldVec3Pool().getVecFromPool(living.posX, living.posY + 1.62F, living.posZ);
			Vec3 lookVec = living.getLook(1.0f);
			Vec3 targetVec = posVec.addVector(lookVec.xCoord * 10f, lookVec.yCoord * 10f, lookVec.zCoord * 10f);
			MovingObjectPosition mop = living.worldObj.clip(posVec, targetVec);
			map.put("IsLookingAtBlock", false);
			if (mop != null) {
				map.put("IsLookingAtBlock", mop.typeOfHit == EnumMovingObjectType.TILE);
				if (mop.typeOfHit == EnumMovingObjectType.TILE) {
					int blockId = living.worldObj.getBlockId(mop.blockX, mop.blockY, mop.blockZ);
					HashMap lookingAt = new HashMap();
					if (relativePos != null) {
						lookingAt.put("x", mop.blockX - relativePos.xCoord);
						lookingAt.put("y", mop.blockY - relativePos.yCoord);
						lookingAt.put("z", mop.blockZ - relativePos.zCoord);
					} else {
						lookingAt.put("x", mop.blockX);
						lookingAt.put("y", mop.blockY);
						lookingAt.put("z", mop.blockZ);
					}
					map.put("lookingAt", lookingAt);
				} else {
					map.put("lookingAt", null);
				}
			}
		}

		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entity;
			map.put("type", "Player");
			map.put("inventory", InventoryUtils.invToMap(player.inventory));
			map.put("isAirBorne", player.isAirBorne);
			map.put("isBlocking", player.isBlocking());
			map.put("username", player.username);
			map.put("foodLevel", player.getFoodStats().getFoodLevel());
			map.put("isCreativeMode", player.capabilities.isCreativeMode);
		}
	}
}
