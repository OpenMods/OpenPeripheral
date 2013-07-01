package openperipheral.common.sensor;

import java.util.HashMap;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.Vec3;

public class MinecartData extends HashMap implements IEntityData {

	private HashMap position = new HashMap();
	private HashMap inventory = new HashMap();
	
	public MinecartData() {
		put("position", position);
	}
	
	public void fromEntity(Vec3 sensorPosition, EntityMinecart minecart) {
		position.put("x", minecart.posX - sensorPosition.xCoord);
		position.put("y", minecart.posY - sensorPosition.yCoord);
		position.put("z", minecart.posZ - sensorPosition.zCoord);
	}
}
