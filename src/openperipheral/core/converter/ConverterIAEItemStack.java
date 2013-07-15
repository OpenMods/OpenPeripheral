package openperipheral.core.converter;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import openperipheral.api.ITypeConverter;
import openperipheral.core.util.InventoryUtils;
import appeng.api.IAEItemStack;
import appeng.api.IAETagCompound;

public class ConverterIAEItemStack implements ITypeConverter {

	@Override
	public Object fromLua(Object o, Class required) {
		if (required == IAEItemStack.class && o instanceof Map) {
			Map map = (Map)o;
			int _quantity = 1;
			int _dmg = 0;
			Map m = (Map) o;
			if (!m.containsKey("id")) {
				return null;
			}
			final int id = (int) (double) (Double) m.get("id");
			if (m.containsKey("qty")) {
				_quantity = (int) (double) (Double) m.get("qty");
			}
			if (m.containsKey("dmg")) {
				_dmg = (int) (double) (Double) m.get("dmg");
			}
			final int quantity = _quantity;
			final int dmg = _dmg;
			return new OpenPIAEItemStack(id, dmg, (long)quantity);
		}
		return null;
	}

	@Override
	public Object toLua(Object o) {
		if (o instanceof IAEItemStack) {
			HashMap map = new HashMap();
			IAEItemStack stack = (IAEItemStack) o;
			ItemStack iStack = new ItemStack(stack.getItem(), 1, stack.getItemDamage());
			map.put("id", stack.getItemID());
			map.put("name", InventoryUtils.getNameForItemStack(iStack));
			map.put("rawName",  InventoryUtils.getRawNameForStack(iStack));
			map.put("qty", stack.getStackSize());
			map.put("dmg", stack.getItemDamage());
		}
		return null;
	}

}
