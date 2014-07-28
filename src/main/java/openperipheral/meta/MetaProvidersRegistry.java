package openperipheral.meta;

import java.util.*;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import openperipheral.api.IEntityMetadataProvider;
import openperipheral.api.IItemStackMetadataProvider;
import openperipheral.api.IMetaProvider;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;

public abstract class MetaProvidersRegistry<P extends IMetaProvider<?>> {

	private static <T extends IMetaProvider<?>> MetaProvidersRegistry<T> create(final Class<?> baseCls) {
		return new MetaProvidersRegistry<T>() {
			@Override
			protected boolean validateCls(Class<?> cls) {
				return baseCls.isAssignableFrom(cls);
			}
		};
	}

	public static final MetaProvidersRegistry<IEntityMetadataProvider<?>> ENITITES = create(Entity.class);

	public static final MetaProvidersRegistry<IItemStackMetadataProvider<?>> ITEMS = create(Item.class);

	private final Multimap<Class<?>, P> directProviders = HashMultimap.create();

	private final Multimap<Class<?>, P> allProviders = HashMultimap.create();

	private final Set<Class<?>> inCache = Sets.newHashSet();

	protected abstract boolean validateCls(Class<?> targetCls);

	public void addProvider(P provider) {
		final Class<?> targetClass = provider.getTargetClass();

		Preconditions.checkArgument(targetClass.isInterface() || validateCls(targetClass),
				"Invalid type: %s", targetClass);
		directProviders.put(targetClass, provider);
		allProviders.clear();
		inCache.clear();
	}

	public Collection<? extends P> getProviders(Class<?> cls) {
		if (!inCache.contains(cls)) {
			Collection<P> providers = collectProviders(cls);
			allProviders.putAll(cls, providers);
			inCache.add(cls);
			return providers;
		}

		return allProviders.get(cls);
	}

	private Set<P> collectProviders(Class<?> targetCls) {
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

		Class<?> cls;
		while ((cls = queue.poll()) != null) {
			classes.add(cls);
			queue.add(cls.getSuperclass());
			queue.addAll(Arrays.asList(cls.getInterfaces()));
		}
		return classes;
	}
}
