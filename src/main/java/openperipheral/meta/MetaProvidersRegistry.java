package openperipheral.meta;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import openmods.Log;
import openperipheral.api.meta.IEntityMetaProvider;
import openperipheral.api.meta.IItemStackMetaProvider;
import openperipheral.api.meta.IMetaProvider;

public class MetaProvidersRegistry<P extends IMetaProvider<?>> {

	private static <T extends IMetaProvider<?>> MetaProvidersRegistry<T> create(Class<?> baseCls, String type) {
		return new MetaProvidersRegistry<T>(type, baseCls);
	}

	public static final MetaProvidersRegistry<IEntityMetaProvider<?>> ENITITES = create(Entity.class, "entity");

	public static final MetaProvidersRegistry<IItemStackMetaProvider<?>> ITEMS = create(Item.class, "item");

	private final Multimap<Class<?>, P> directProviders = ArrayListMultimap.create();

	private final Map<Class<?>, Map<String, P>> providersCache = Maps.newHashMap();

	private final String type;

	private final Class<?> baseClass;

	public MetaProvidersRegistry(String type, Class<?> baseClass) {
		this.type = type;
		this.baseClass = baseClass;
	}

	public void addProvider(P provider) {
		final Class<?> targetClass = provider.getTargetClass();

		Preconditions.checkArgument(targetClass.isInterface() || baseClass.isAssignableFrom(targetClass),
				"Invalid type: %s", targetClass);

		directProviders.put(targetClass, provider);

		Log.trace("Registering %s metadata provider '%s' for '%s'", type, provider.getClass(), targetClass);
		providersCache.clear();
	}

	public Map<String, P> getProviders(Class<?> cls) {
		Map<String, P> providerMap = providersCache.get(cls);

		if (providerMap == null) {
			Set<P> providers = collectAllProviders(cls);

			providerMap = Maps.newHashMap();
			for (P provider : providers) {
				final String key = provider.getKey();
				P previous = providerMap.put(key, provider);
				Preconditions.checkState(previous == null, "Duplicate meta provider for key %s on class %s: %s -> %s", key, cls, previous, provider);
			}

			providerMap = ImmutableMap.copyOf(providerMap);
			providersCache.put(cls, providerMap);
		}

		return providerMap;
	}

	private Set<P> collectAllProviders(Class<?> targetCls) {
		Set<P> providers = Sets.newIdentityHashSet();
		for (Class<?> cls : getAllImplementedClasses(targetCls))
			providers.addAll(directProviders.get(cls));

		return providers;
	}

	private static Set<Class<?>> getAllImplementedClasses(Class<?> targetCls) {
		Set<Class<?>> classes = Sets.newHashSet();
		Queue<Class<?>> queue = Lists.newLinkedList();
		queue.add(targetCls);

		while (!queue.isEmpty()) {
			Class<?> cls = queue.poll();
			classes.add(cls);
			final Class<?> superclass = cls.getSuperclass();
			if (superclass != null) queue.add(superclass);
			queue.addAll(Arrays.asList(cls.getInterfaces()));
		}
		return classes;
	}
}
