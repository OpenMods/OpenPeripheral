package openperipheral.meta;

import java.util.Arrays;
import java.util.Queue;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import openmods.Log;
import openperipheral.api.IEntityMetaProvider;
import openperipheral.api.IItemStackMetaProvider;
import openperipheral.api.IMetaProvider;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;

public class MetaProvidersRegistry<P extends IMetaProvider<?>> {

	private static <T extends IMetaProvider<?>> MetaProvidersRegistry<T> create(Class<?> baseCls, String type) {
		return new MetaProvidersRegistry<T>(type, baseCls);
	}

	public static final MetaProvidersRegistry<IEntityMetaProvider<?>> ENITITES = create(Entity.class, "entity");

	public static final MetaProvidersRegistry<IItemStackMetaProvider<?>> ITEMS = create(Item.class, "item");

	private final Multimap<Class<?>, P> directProviders = ArrayListMultimap.create();

	private final Multimap<Class<?>, P> providersCache = ArrayListMultimap.create();

	private final Set<Class<?>> inCache = Sets.newHashSet();

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
		inCache.clear();
	}

	public Iterable<? extends P> getProviders(Class<?> cls) {
		Iterable<? extends P> all;

		if (!inCache.contains(cls)) {
			all = collectAllProviders(cls);
			providersCache.putAll(cls, all);
			inCache.add(cls);
		} else {
			all = providersCache.get(cls);
		}

		return all;
	}

	private Set<P> collectAllProviders(Class<?> targetCls) {
		Set<P> providers = Sets.newIdentityHashSet();
		for (Class<?> cls : getAllImplementedClasses(targetCls))
			providers.addAll(directProviders.get(cls));

		Set<String> keys = Sets.newHashSet();
		for (P provider : providers) {
			final String key = provider.getKey();
			boolean isNew = keys.add(key);
			Preconditions.checkState(isNew, "Meta provider key %s is duplicated for class %s", key, targetCls);
		}

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
