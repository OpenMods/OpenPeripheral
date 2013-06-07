package openperipheral.converter;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import openperipheral.ITypeConverter;

public class ConverterItemStack implements ITypeConverter {

	@Override
	public Object fromLua(Object o, Class required) {
		if (required == ItemStack.class && o instanceof Map) {
			int quantity = 1;
			int dmg = 0;
			Map m = (Map) o;
			if (!m.containsKey("id")) {
				return null;
			}
			int id = (int)(double)(Double) m.get("id");
			if (m.containsKey("qty")) {
				quantity = (int)(double)(Double) m.get("qty");
			}
			if (m.containsKey("dmg")) {
				dmg = (int)(double)(Double) m.get("dmg");
			}
			return new ItemStack(id, quantity, dmg);
		}
		return null;
	}

	@Override
	public Object toLua(Object o) {
		if (o instanceof ItemStack) {
			ItemStack s = (ItemStack) o;
			HashMap ret = new HashMap();
			ret.put("id", s.itemID);
			ret.put("qty", s.stackSize);
			ret.put("dmg", s.getItemDamage());
			return ret;
		}
		return null;
	}

}
