package openperipheral.meta;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import openperipheral.ApiImplementation;
import openperipheral.api.IEntityMetaBuilder;
import openperipheral.api.IEntityMetaProvider;

import com.google.common.collect.Maps;

@ApiImplementation
public class EntityMetadataBuilder implements IEntityMetaBuilder {

	@Override
	public Map<String, Object> getEntityMetadata(Entity entity, Vec3 relativePos) {
		return fillProperties(entity, relativePos);
	}

	private static Map<String, Object> fillProperties(Entity entity, Vec3 relativePos) {
		Map<String, Object> map = Maps.newHashMap();
		fillBasicProperties(map, entity, relativePos);
		fillCustomProperties(map, entity, relativePos);
		return map;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void fillCustomProperties(Map<String, Object> map, Entity entity, Vec3 relativePos) {
		final Iterable<IEntityMetaProvider<?>> providers = MetaProvidersRegistry.ENITITES.getProviders(entity.getClass());

		for (IEntityMetaProvider provider : providers) {
			Object converted = provider.getMeta(entity, relativePos);
			if (converted != null) {
				final String key = provider.getKey();
				map.put(key, converted);
			}
		}
	}

	private static void fillBasicProperties(Map<String, Object> map, Entity entity, Vec3 relativePos) {
		addPositionInfo(map, entity, relativePos);
		map.put("name", entity.getCommandSenderName());
		map.put("id", entity.getEntityId());
		map.put("uuid", entity.getUniqueID());

		if (entity.riddenByEntity != null) {
			map.put("riddenBy", fillProperties(entity.riddenByEntity, relativePos));
		}

		if (entity.ridingEntity != null) {
			map.put("ridingEntity", entity.ridingEntity.getEntityId());
		}
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

	@Override
	public void register(IEntityMetaProvider<?> provider) {
		MetaProvidersRegistry.ENITITES.addProvider(provider);
	}
}
