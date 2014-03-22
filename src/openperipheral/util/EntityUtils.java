package openperipheral.util;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import openperipheral.IntegrationModuleRegistry;

import com.google.common.collect.Maps;

public class EntityUtils {

	public static Map<String, Object> entityToMap(Entity entity, ChunkCoordinates relativePos) {
		Vec3 vec = Vec3.createVectorHelper(relativePos.posX, relativePos.posY, relativePos.posZ);
		return entityToMap(entity, vec);
	}

	public static Map<String, Object> entityToMap(Entity entity, Vec3 relativePos) {

		Map<String, Object> map = Maps.newHashMap();

		addPositionInfo(map, entity, relativePos);
		map.put("type", entity.getEntityName());

		if (entity.riddenByEntity != null) {
			map.put("riddenBy", entityToMap(entity.riddenByEntity, relativePos));
		}

		if (entity.ridingEntity != null) {
			map.put("ridingEntity", entity.ridingEntity.entityId);
		}

		IntegrationModuleRegistry.appendEntityInfo(map, entity, relativePos);

		return map;
	}

	private static void addPositionInfo(Map<String, Object> map, Entity entity, Vec3 relativePos) {
		Map<Object, Object> position = Maps.newHashMap();

		if (relativePos != null) {
			position.put("x", entity.posX - relativePos.xCoord);
			position.put("y", entity.posY - relativePos.yCoord);
			position.put("z", entity.posZ - relativePos.zCoord);
		} else {
			position.put("x", entity.posX);
			position.put("y", entity.posY);
			position.put("z", entity.posZ);
		}
		map.put("position", position);
	}

}
