package openperipheral.sensor.common;

import java.util.Collection;
import java.util.HashMap;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import openperipheral.core.util.InventoryUtils;

public class PlayerData extends MobData implements IEntityData {

	private HashMap inventory = new HashMap();
	
	public PlayerData() {
		put("slots", inventory);
	}
	
	public void fromEntity(Vec3 sensorPosition, EntityPlayer player) {
		
		super.fromEntity(sensorPosition, player);

		put("username", player.username);
		put("foodLevel", player.getFoodStats().getFoodLevel());
		put("isCreativeMode", player.capabilities.isCreativeMode);
		InventoryUtils.invToMap(inventory, player.inventory);
	}
	
}
