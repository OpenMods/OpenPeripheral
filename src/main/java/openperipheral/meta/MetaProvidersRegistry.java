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

public abstract class MetaProvidersRegistry<P extends IMetaProvider<?>> {

	private static <T extends IMetaProvider<?>> MetaProvidersRegistry<T> create(final Class<?> baseCls, final String type) {
		return new MetaProvidersRegistry<T>() {
			@Override
			protected boolean validateCls(Class<?> cls) {
				return baseCls.isAssignableFrom(cls);
			}

			@Override
			protected String type() {
				return type;
			}
		};
	}

	public static final MetaProvidersRegistry<IEntityMetaProvider<?>> ENITITES = create(Entity.class, "entity");

	public static final MetaProvidersRegistry<IItemStackMetaProvider<?>> ITEMS = create(Item.class, "item");

	private final Multimap<Class<?>, P> directProviders = HashMultimap.create();

	private final Set<P> genericProviders = Sets.newIdentityHashSet();

	private final Multimap<Class<?>, P> specificProvidersCache = HashMultimap.create();

	private final Set<Class<?>> inCache = Sets.newHashSet();

	protected abstract String type();

	protected abstract boolean validateCls(Class<?> targetCls);

	public void addProvider(P provider) {
		final Class<?> targetClass = provider.getTargetClass();

		Preconditions.checkArgument(targetClass.isInterface() || validateCls(targetClass),
				"Invalid type: %s", targetClass);

		if (targetClass == Object.class) genericProviders.add(provider);
		else directProviders.put(targetClass, provider);

		Log.trace("Registering %s metadata provider '%s' for '%s'", type(), provider.getClass(), targetClass);
		specificProvidersCache.clear();
		inCache.clear();
	}

	public Iterable<? extends P> getProviders(Class<?> cls) {
		Iterable<? extends P> specific;

		if (!inCache.contains(cls)) {
			specific = collectProviders(cls);
			specificProvidersCache.putAll(cls, specific);
			inCache.add(cls);
		} else {
			specific = specificProvidersCache.get(cls);
		}

		return Iterables.concat(specificProvidersCache.get(cls), specific);
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
