package openperipheral.core.integration;

import java.util.Map;
import openperipheral.core.AdapterManager;
import openperipheral.core.adapter.mystcraft.AdapterWritingDesk;

import com.xcompwiz.mystcraft.api.items.IItemRenameable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleMystcraft {

  public static void init() {
    AdapterManager.addPeripheralAdapter(new AdapterWritingDesk());

  }
	public static void appendBookInfo(Map map, ItemStack stack) {
		if (stack != null && stack.getItem() instanceof IItemRenameable) {
			if (stack.hasTagCompound()) {
				NBTTagCompound tag = stack.getTagCompound();
				if (tag.hasKey("agename")) {
					map.put("destination", tag.getString("agename"));
				}
			}
		}
		
	}
}
