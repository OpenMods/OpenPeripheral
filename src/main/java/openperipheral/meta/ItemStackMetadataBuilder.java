package openperipheral.meta;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;
import openperipheral.api.adapter.method.ScriptObject;
import openperipheral.api.meta.IItemStackCustomMetaProvider;
import openperipheral.api.meta.IItemStackMetaProvider;
import openperipheral.api.meta.IItemStackPartialMetaBuilder;
import openperipheral.api.meta.IMetaProviderProxy;

public class ItemStackMetadataBuilder implements IItemStackPartialMetaBuilder {

	@ScriptObject
	private static class Proxy implements IMetaProviderProxy {
		private final Map<String, IItemStackMetaProvider<?>> providers;
		private final ItemStack stack;
		private final Item item;

		private Proxy(Map<String, IItemStackMetaProvider<?>> providers, ItemStack stack, Item item) {
			this.providers = ImmutableMap.copyOf(providers);
			this.stack = stack;
			this.item = item;
		}

		@Override
		public Map<String, Object> basic() {
			return createBasicProperties(item, stack);
		}

		@Override
		public Map<String, Object> all() {
			Map<String, Object> map = basic();
			fillCustomProperties(map, providers.values(), item, stack);
			return map;
		}

		@Override
		public Set<String> keys() {
			return ImmutableSet.copyOf(providers.keySet());
		}

		@Override
		public Map<String, Object> select(String... keys) {
			Item item = stack.getItem();
			if (item == null) return ImmutableMap.of();

			Map<String, Object> result = basic();

			for (String key : keys) {
				IItemStackMetaProvider<?> provider = providers.get(key);
				if (provider != null) {
					Object value = getProperty(stack, item, provider);
					if (value != null) result.put(key, value);
				}
			}

			return result;
		}

		@Override
		public Object single(String key) {
			IItemStackMetaProvider<?> provider = providers.get(key);
			return provider != null? getProperty(stack, item, provider) : null;
		}
	}

	private static final Map<String, Object> NULL;

	static {
		ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
		builder.put("id", "invalid");
		NULL = builder.build();
	}

	private static Map<String, Object> createBasicProperties(Item item, ItemStack itemstack) {
		Map<String, Object> map = Maps.newHashMap();
		final ResourceLocation id = GameData.getItemRegistry().getNameForObject(item);

		map.put("id", id != null? id.toString() : "?");
		map.put("name", id != null? id.getResourcePath() : "?");
		map.put("mod_id", id != null? id.getResourceDomain() : "?");

		map.put("display_name", getNameForItemStack(itemstack));
		map.put("raw_name", getRawNameForStack(itemstack));
		map.put("qty", itemstack.stackSize);
		map.put("dmg", itemstack.getItemDamage());

		if (item.showDurabilityBar(itemstack)) map.put("health_bar", item.getDurabilityForDisplay(itemstack));
		map.put("max_dmg", itemstack.getMaxDamage());
		map.put("max_size", itemstack.getMaxStackSize());

		return map;
	}

	private static void fillCustomProperties(Map<String, Object> map, Iterable<IItemStackMetaProvider<?>> providers, Item item, ItemStack itemstack) {
		for (IItemStackMetaProvider<?> provider : providers) {
			Object converted = getProperty(itemstack, item, provider);
			if (converted != null) {
				final String key = provider.getKey();
				map.put(key, converted);
			}
		}
	}

	private static String getNameForItemStack(ItemStack is) {
		try {
			return is.getDisplayName();
		} catch (Exception e) {}

		try {
			return is.getUnlocalizedName();
		} catch (Exception e2) {}

		return "unknown";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object getProperty(ItemStack stack, Item item, IItemStackMetaProvider provider) {
		return provider.getMeta(item, stack);
	}

	private static Map<String, IItemStackMetaProvider<?>> getProviders(Item item, ItemStack stack) {
		final Map<String, IItemStackMetaProvider<?>> immutableProviders = MetaProvidersRegistry.ITEMS.getProviders(item.getClass());
		final Map<String, IItemStackMetaProvider<?>> providers = Maps.newHashMap(immutableProviders);
		filterCustomProviders(providers, item, stack);
		return providers;
	}

	@SuppressWarnings("unchecked")
	private static void filterCustomProviders(Map<String, IItemStackMetaProvider<?>> providers, Item item, ItemStack stack) {
		Iterator<IItemStackMetaProvider<?>> it = providers.values().iterator();

		while (it.hasNext()) {
			final IItemStackMetaProvider<?> provider = it.next();
			if ((provider instanceof IItemStackCustomMetaProvider) &&
					!((IItemStackCustomMetaProvider<Item>)provider).canApply(item, stack))
				it.remove();
		}
	}

	public static String getRawNameForStack(ItemStack is) {
		try {
			return is.getUnlocalizedName().toLowerCase(Locale.ENGLISH);
		} catch (Exception e) {}

		return "unknown";
	}

	@Override
	public IMetaProviderProxy createProxy(final ItemStack stack) {
		final Item item = stack.getItem();
		if (item == null) return null;

		final Map<String, IItemStackMetaProvider<?>> providers = getProviders(item, stack);
		return new Proxy(providers, stack, item);
	}

	@Override
	public Map<String, Object> getBasicItemStackMetadata(ItemStack itemstack) {
		if (itemstack == null) return NULL;
		Item item = itemstack.getItem();

		return createBasicProperties(item, itemstack);
	}

	@Override
	public Map<String, Object> getItemStackMetadata(ItemStack itemstack) {
		if (itemstack == null) return NULL;
		Item item = itemstack.getItem();

		Map<String, Object> map = createBasicProperties(item, itemstack);

		final Iterable<IItemStackMetaProvider<?>> providers = getProviders(item, itemstack).values();
		fillCustomProperties(map, providers, item, itemstack);
		return map;
	}

	@Override
	public Object getItemStackMetadata(String key, ItemStack stack) {
		Item item = stack.getItem();
		if (item == null) return null;

		Map<String, IItemStackMetaProvider<?>> providers = getProviders(item, stack);

		IItemStackMetaProvider<?> provider = providers.get(key);
		return provider != null? getProperty(stack, item, provider) : null;
	}

	@Override
	public Set<String> getKeys(ItemStack target) {
		return ImmutableSet.copyOf(getProviders(target.getItem(), target).keySet());
	}

	@Override
	public void register(IItemStackMetaProvider<?> provider) {
		MetaProvidersRegistry.ITEMS.addProvider(provider);
	}
}
