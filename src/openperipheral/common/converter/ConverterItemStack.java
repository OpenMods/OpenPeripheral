package openperipheral.common.converter;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import openperipheral.api.ITypeConverter;
import openperipheral.common.util.InventoryUtils;

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
			int id = (int) (double) (Double) m.get("id");
			if (m.containsKey("qty")) {
				quantity = (int) (double) (Double) m.get("qty");
			}
			if (m.containsKey("dmg")) {
				dmg = (int) (double) (Double) m.get("dmg");
			}
			return new ItemStack(id, quantity, dmg);
		}
		return null;
	}

	@Override
	public Object toLua(Object o) {
		if (o instanceof ItemStack) {
			HashMap ret = new HashMap();
			InventoryUtils.itemstackToMap(ret, (ItemStack) o);
			return ret;
		}
		return null;
	}

}
