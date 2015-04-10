package openperipheral.meta;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import openperipheral.ApiImplementation;
import openperipheral.api.adapter.method.ScriptObject;
import openperipheral.api.meta.*;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

@ApiImplementation
public class EntityMetadataBuilder implements IEntityPartialMetaBuilder {

	@ScriptObject
	private static class Proxy implements IMetaProviderProxy {
		private final Map<String, IEntityMetaProvider<?>> providers;
		private final Vec3 relativePos;
		private final Entity entity;

		private Proxy(Map<String, IEntityMetaProvider<?>> providers, Vec3 relativePos, Entity entity) {
			this.providers = ImmutableMap.copyOf(providers);
			this.relativePos = relativePos;
			this.entity = entity;
		}

		@Override
		public Map<String, Object> basic() {
			return createBasicProperties(entity, relativePos);
		}

		@Override
		public Map<String, Object> all() {
			Map<String, Object> map = basic();
			fillCustomProperties(map, providers.values(), entity, relativePos);
			return map;
		}

		@Override
		public Set<String> keys() {
			return ImmutableSet.copyOf(providers.keySet());
		}

		@Override
		public Map<String, Object> select(String... keys) {
			Map<String, Object> result = basic();

			for (String key : keys) {
				IEntityMetaProvider<?> provider = providers.get(key);
				if (provider != null) {
					Object value = getProperty(entity, relativePos, provider);
					if (value != null) result.put(key, value);
				}
			}

			return result;
		}

		@Override
		public Object single(String key) {
			IEntityMetaProvider<?> provider = providers.get(key);
			return provider != null? getProperty(entity, relativePos, provider) : null;
		}
	}

	private static void fillCustomProperties(Map<String, Object> map, final Iterable<IEntityMetaProvider<?>> providers, Entity entity, Vec3 relativePos) {
		for (IEntityMetaProvider<?> provider : providers) {
			Object converted = getProperty(entity, relativePos, provider);
			if (converted != null) {
				final String key = provider.getKey();
				map.put(key, converted);
			}
		}
	}

	private static Map<String, Object> createBasicProperties(Entity entity, Vec3 relativePos) {
		Map<String, Object> map = Maps.newHashMap();
		addPositionInfo(map, entity, relativePos);
		map.put("name", entity.getCommandSenderName());
		map.put("id", entity.getEntityId());
		map.put("uuid", entity.getUniqueID());

		if (entity.riddenByEntity != null) {
			map.put("riddenBy", entity.riddenByEntity.getEntityId());
		}

		if (entity.ridingEntity != null) {
			map.put("ridingEntity", entity.ridingEntity.getEntityId());
		}
		return map;
	}

	private static void addPositionInfo(Map<String, Object> map, Entity entity, Vec3 relativePos) {
		Map<String, Double> position = (relativePos != null)
				? addRelativePos(entity, relativePos)
				: addAbsolutePos(entity);

		map.put("position", position);
	}

	private static Map<String, Double> addAbsolutePos(Entity entity) {
		return createPosition(entity.posX, entity.posY, entity.posZ);
	}

	private static Map<String, Double> addRelativePos(Entity entity, Vec3 relativePos) {
		return createPosition(entity.posX - relativePos.xCoord, entity.posY - relativePos.yCoord, entity.posZ - relativePos.zCoord);
	}

	private static Map<String, Double> createPosition(double x, double y, double z) {
		return ImmutableMap.of("x", x, "y", y, "z", z);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object getProperty(Entity entity, Vec3 relativePos, IEntityMetaProvider provider) {
		return provider.getMeta(entity, relativePos);
	}

	protected static Map<String, IEntityMetaProvider<?>> getProviders(Entity entity) {
		final Map<String, IEntityMetaProvider<?>> immutableProviders = MetaProvidersRegistry.ENITITES.getProviders(entity.getClass());
		final Map<String, IEntityMetaProvider<?>> providers = Maps.newHashMap(immutableProviders);
		filterCustomProviders(providers, entity);
		return providers;
	}

	@SuppressWarnings("unchecked")
	protected static void filterCustomProviders(Map<String, IEntityMetaProvider<?>> providers, Entity entity) {
		Iterator<IEntityMetaProvider<?>> it = providers.values().iterator();

		while (it.hasNext()) {
			final IEntityMetaProvider<?> provider = it.next();
			if ((provider instanceof IEntityCustomMetaProvider) &&
					((IEntityCustomMetaProvider<Entity>)provider).canApply(entity)) it.remove();
		}
	}

	@Override
	public IMetaProviderProxy createProxy(Entity entity, Vec3 relativePos) {
		final Map<String, IEntityMetaProvider<?>> providers = getProviders(entity);
		return new Proxy(providers, relativePos, entity);
	}

	@Override
	public Map<String, Object> getBasicEntityMetadata(Entity entity, Vec3 relativePos) {
		return createBasicProperties(entity, relativePos);
	}

	@Override
	public Map<String, Object> getEntityMetadata(Entity entity, Vec3 relativePos) {
		Map<String, Object> map = createBasicProperties(entity, relativePos);

		final Iterable<IEntityMetaProvider<?>> providers = getProviders(entity).values();
		fillCustomProperties(map, providers, entity, relativePos);
		return map;
	}

	@Override
	public Object getEntityMetadata(String key, Entity entity, Vec3 relativePos) {
		Map<String, IEntityMetaProvider<?>> providers = getProviders(entity);

		IEntityMetaProvider<?> provider = providers.get(key);
		return provider != null? getProperty(entity, relativePos, provider) : null;
	}

	@Override
	public Set<String> getKeys(Entity target) {
		return ImmutableSet.copyOf(getProviders(target).keySet());
	}

	@Override
	public void register(IEntityMetaProvider<?> provider) {
		MetaProvidersRegistry.ENITITES.addProvider(provider);
	}
}
