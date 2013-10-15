package openperipheral.core.util;

import java.util.HashMap;
import java.util.Map;

import openperipheral.core.integration.ModuleForestry;
import openperipheral.core.integration.ModuleMystcraft;
import openperipheral.core.integration.ModuleRailcraft;
import openperipheral.core.integration.ModuleVanilla;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public class EntityUtils {

	public static Map entityToMap(Entity entity, Vec3 relativePos) {

		HashMap map = new HashMap();
		HashMap position = new HashMap();

		map.put("position", position);

		if (relativePos != null) {
			position.put("x", entity.posX - relativePos.xCoord);
			position.put("y", entity.posY - relativePos.yCoord);
			position.put("z", entity.posZ - relativePos.zCoord);
		} else {
			position.put("x", entity.posX);
			position.put("y", entity.posY);
			position.put("z", entity.posZ);
		}

		map.put("type", entity.getEntityName());

		if (entity.riddenByEntity != null) {
			map.put("riddenBy", entityToMap(entity.riddenByEntity, relativePos));
		}

		if (entity.ridingEntity != null) {
			map.put("ridingEntity", entity.ridingEntity.entityId);
		}
		
		// Add mod entity mappings
		ModuleVanilla.entityToMap(entity, map, relativePos);
		ModuleForestry.entityToMap(entity, map, relativePos);
		ModuleMystcraft.entityToMap(entity, map, relativePos);
		ModuleRailcraft.entityToMap(entity, map, relativePos);

		return map;
	}

}
