package openperipheral.converter;

import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openperipheral.api.converter.IConverter;
import openperipheral.meta.ItemStackMetadataBuilder;

import com.google.common.base.Preconditions;

import cpw.mods.fml.common.registry.GameRegistry;

public class ConverterItemStack extends GenericConverterAdapter {

	private ItemStackMetadataBuilder BUILDER = new ItemStackMetadataBuilder();

	@Override
	public Object fromLua(IConverter registry, Object o, Class<?> required) {
		if (required == ItemStack.class && o instanceof Map) {
			Map<?, ?> m = (Map<?, ?>)o;

			// TODO check
			Object id = m.get("id");
			Preconditions.checkArgument(id instanceof String, "Invalid item id");

			String[] parts = ((String)id).split(":");
			Preconditions.checkArgument(parts.length == 2, "Invalid item id");
			String modId = parts[0];
			String name = parts[1];
			Item item = GameRegistry.findItem(modId, name);

			int quantity = getIntValue(m, "qty", 1);
			int dmg = getIntValue(m, "dmg", 0);

			return new ItemStack(item, quantity, dmg);
		}
		return null;
	}

	private static int getIntValue(Map<?, ?> map, String key, int _default) {
		Object value = map.get(key);
		if (value instanceof Number) return ((Number)value).intValue();

		return _default;
	}

	@Override
	public Object toLua(IConverter registry, Object o) {
		if (o instanceof ItemStack) {
			Object meta = BUILDER.getItemStackMetadata((ItemStack)o);
			return registry.toLua(meta);
		}

		return null;
	}

}
