package openperipheral.implementations;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import openperipheral.IntegrationModuleRegistry;
import openperipheral.api.IEntityMetaProvider;

import com.google.common.collect.Maps;

@ApiImplementation
public class EntityMetaProviderImpl implements IEntityMetaProvider {
	@Override
	public Map<String, Object> getEntityMetadata(Entity entity, Vec3 relativePos) {

		Map<String, Object> map = Maps.newHashMap();

		addPositionInfo(map, entity, relativePos);
		map.put("type", entity.getEntityName());

		if (entity.riddenByEntity != null) {
			map.put("riddenBy", getEntityMetadata(entity.riddenByEntity, relativePos));
		}

		if (entity.ridingEntity != null) {
			map.put("ridingEntity", entity.ridingEntity.entityId);
		}

		IntegrationModuleRegistry.appendEntityInfo(map, entity, relativePos);

		return map;
	}

	private static void addPositionInfo(Map<String, Object> map, Entity entity, Vec3 relativePos) {
		Map<String, Object> position = (relativePos != null)
				? addRelativePos(entity, relativePos)
				: addAbsolutePos(entity);

		map.put("position", position);
	}

	private static Map<String, Object> addAbsolutePos(Entity entity) {
		Map<String, Object> position = Maps.newHashMap();
		position.put("x", entity.posX);
		position.put("y", entity.posY);
		position.put("z", entity.posZ);
		return position;
	}

	private static Map<String, Object> addRelativePos(Entity entity, Vec3 relativePos) {
		Map<String, Object> position = Maps.newHashMap();
		position.put("x", entity.posX - relativePos.xCoord);
		position.put("y", entity.posY - relativePos.yCoord);
		position.put("z", entity.posZ - relativePos.zCoord);
		return position;
	}
}
