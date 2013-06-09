package openperipheral.converter;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidStack;
import openperipheral.ITypeConverter;

public class ConverterILiquidTank implements ITypeConverter {

	@Override
	public Object fromLua(Object o, Class required) {
		return null;
	}

	@Override
	public Object toLua(Object o) {
		if (o instanceof ILiquidTank) {
			ILiquidTank t = (ILiquidTank) o;
			Map map = new HashMap();
			map.put("capacity", t.getCapacity());
			map.put("pressure", t.getTankPressure());
			LiquidStack lyqyd = t.getLiquid();
			if (lyqyd != null) {
				map.put("amount", lyqyd.amount);
				map.put("id", lyqyd.itemID);
				map.put("name", Item.itemsList[lyqyd.itemID].getUnlocalizedName().substring(4));
			}
			return map;
		}
		return null;
	}

}
