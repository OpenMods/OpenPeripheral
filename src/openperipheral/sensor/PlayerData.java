package openperipheral.sensor;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import openperipheral.core.util.InventoryUtils;

public class PlayerData extends MobData implements IEntityData {

	private HashMap inventory = new HashMap();
	
	public PlayerData() {
		put("slots", inventory);
	}
	
	public void fromEntity(Vec3 sensorPosition, EntityPlayer player) {
		super.fromEntity(sensorPosition, player);
		put("isAirBorne", player.isAirBorne);
		put("isBlocking", player.isBlocking());
		put("username", player.username);
		put("foodLevel", player.getFoodStats().getFoodLevel());
		put("isCreativeMode", player.capabilities.isCreativeMode);
		InventoryUtils.invToMap(inventory, player.inventory);
	}
	
}
