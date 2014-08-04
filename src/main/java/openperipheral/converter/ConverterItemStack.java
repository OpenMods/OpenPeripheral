package openperipheral.converter;

import java.util.Map;

import net.minecraft.item.ItemStack;
import openperipheral.api.ITypeConverter;
import openperipheral.api.ITypeConvertersRegistry;
import openperipheral.meta.ItemStackMetadataBuilder;

public class ConverterItemStack implements ITypeConverter {

	private ItemStackMetadataBuilder BUILDER = new ItemStackMetadataBuilder();

	@Override
	public Object fromLua(ITypeConvertersRegistry registry, Object o, Class<?> required) {
		if (required == ItemStack.class && o instanceof Map) {
			Map<?, ?> m = (Map<?, ?>)o;

			if (!m.containsKey("id")) return null;
			int id = ((Number)m.get("id")).intValue();
			int quantity = getIntValue(m, "qty", 1);
			int dmg = getIntValue(m, "dmg", 0);

			return new ItemStack(id, quantity, dmg);
		}
		return null;
	}

	private static int getIntValue(Map<?, ?> map, String key, int _default) {
		Object value = map.get(key);
		if (value instanceof Number) return ((Number)value).intValue();

		return _default;
	}

	@Override
	public Object toLua(ITypeConvertersRegistry registry, Object o) {
		return (o instanceof ItemStack)? BUILDER.getItemStackMetadata((ItemStack)o) : null;
	}

}
