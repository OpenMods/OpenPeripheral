package openperipheral.meta;

import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openperipheral.ApiImplementation;
import openperipheral.api.IItemStackMetadataBuilder;
import openperipheral.api.IItemStackMetadataProvider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

@ApiImplementation
public class ItemStackMetadataBuilder implements IItemStackMetadataBuilder {

	private static String getNameForItemStack(ItemStack is) {
		try {
			return is.getDisplayName();
		} catch (Exception e) {}

		try {
			return is.getUnlocalizedName();
		} catch (Exception e2) {}

		return "unknown";
	}

	public static String getRawNameForStack(ItemStack is) {
		try {
			return is.getUnlocalizedName().toLowerCase();
		} catch (Exception e) {}

		return "unknown";
	}

	private static final Map<String, Object> NULL;

	static {
		ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
		builder.put("id", 0)
				.put("name", "empty")
				.put("rawName", "empty")
				.put("qty", 0)
				.put("dmg", 0)
				.put("maxdmg", 0)
				.put("maxSize", 64);
		NULL = builder.build();
	}

	@Override
	public Map<String, Object> getItemStackMetadata(ItemStack itemstack) {
		if (itemstack == null) return NULL;

		Map<String, Object> map = Maps.newHashMap();

		map.put("id", itemstack.itemID);
		map.put("name", getNameForItemStack(itemstack));
		map.put("rawName", getRawNameForStack(itemstack));
		map.put("qty", itemstack.stackSize);
		map.put("dmg", itemstack.getItemDamage());
		map.put("maxdmg", itemstack.getMaxDamage());
		map.put("maxSize", itemstack.getMaxStackSize());

		Item item = itemstack.getItem();
		@SuppressWarnings("unchecked")
		final Iterable<IItemStackMetadataProvider<Object>> providers = (Iterable<IItemStackMetadataProvider<Object>>)MetaProvidersRegistry.ITEMS.getProviders(item.getClass());

		for (IItemStackMetadataProvider<Object> provider : providers) {
			Object converted = provider.getMeta(item, itemstack);
			if (converted != null) {
				final String key = provider.getKey();
				map.put(key, converted);
			}
		}

		return map;
	}

	@Override
	public void register(IItemStackMetadataProvider<?> provider) {
		MetaProvidersRegistry.ITEMS.addProvider(provider);
	}
}
