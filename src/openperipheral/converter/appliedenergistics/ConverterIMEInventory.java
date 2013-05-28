package openperipheral.converter.appliedenergistics;

import java.util.HashMap;

import openperipheral.ITypeConverter;
import appeng.api.me.util.IMEInventory;
import appeng.api.me.util.IMEInventoryHandler;

public class ConverterIMEInventory implements ITypeConverter {

	@Override
	public Object fromLua(Object o, Class required) {
		return null;
	}

	@Override
	public Object toLua(Object o) {
		if (o instanceof IMEInventory) {
			IMEInventory invent = (IMEInventory) o;
			HashMap res = new HashMap();
			if (o instanceof IMEInventoryHandler) {
				IMEInventoryHandler ih = (IMEInventoryHandler) o;
				res.put("totalBytes", ih.totalBytes());
				res.put("freeBytes", ih.freeBytes());
				res.put("usedBytes", ih.usedBytes());
				res.put("unusedItemCount", ih.unusedItemCount());
				res.put("canHoldNewItem", ih.canHoldNewItem());
				res.put("isPreformatted", ih.isPreformatted());
				res.put("isFuzzyPreformatted", ih.isFuzzyPreformatted());
				res.put("name", ih.getName());
			}
			res.put("storedItemTypes", invent.storedItemTypes());
			res.put("storedItemCount", invent.storedItemCount());
			res.put("remainingItemCount", invent.remainingItemCount());
			res.put("remainingItemTypes", invent.remainingItemTypes());
			res.put("totalItemTypes", invent.getTotalItemTypes());
			return res;
		}
		return null;
	}

}
