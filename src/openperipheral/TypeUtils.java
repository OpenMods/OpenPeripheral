package openperipheral;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidStack;

public class TypeUtils {
	
	public static Object convertToSuitableType(Object o) {
		if (o instanceof ILiquidTank[]) {
			HashMap map = new HashMap();
			int index = 1;
			for (ILiquidTank t : (ILiquidTank[])o) {
				map.put(index++, convertToSuitableType(t));
			}
			return map;
		}else if (o instanceof ILiquidTank) {
			return tankToMap((ILiquidTank)o);
		}else if (o instanceof ItemStack) {
			return itemstackToMap((ItemStack)o);
		}else if (o instanceof ItemStack[]) {
			HashMap map = new HashMap();
			int index = 1;
			for (Object obj : (ItemStack[])o) {
				map.put(index++, convertToSuitableType(obj));
			}
			return map;
		}else if (o instanceof List) {
			HashMap map = new HashMap();
			int index = 1;
			for (Object obj : (List)o) {
				map.put(index++, convertToSuitableType(obj));
			}
			return map;
		}
		return o;
	}
	
	private static Map tankToMap(ILiquidTank o) {
		Map map = new HashMap();
		map.put("capacity", o.getCapacity());
		map.put("pressure", o.getTankPressure());
		LiquidStack lyqyd = o.getLiquid();
		if(lyqyd != null) {
			map.put("amount", lyqyd.amount);
			map.put("id", lyqyd.itemID);
		}
		return map;
	}

	public static HashMap itemstackToMap(ItemStack itemstack) {

		HashMap map = new HashMap();

		if (itemstack == null) {

			map.put("id", 0);
			map.put("size", 0);
			map.put("dmg", 0);
			map.put("max", 64);
			return map;

		} else {

			map.put("id", itemstack.itemID);			
			map.put("size", itemstack.stackSize);
			map.put("dmg", itemstack.getItemDamage());
			map.put("max", itemstack.getMaxStackSize());

		}

		return map;
	}
	
	
	public static ItemStack mapToItemStack(Map map) {
		if (!map.containsKey("id") || !(map.get("id") instanceof Double)) {
			return null;
		}
		int metadata = 0;
		int quantity = 1;
		if (map.containsKey("dmg") && map.get("dmg") instanceof Double) {
			metadata = (int)(double)(Double) map.get("dmg");
		}
		if (map.containsKey("size") && map.get("size") instanceof Double) {
			quantity = (int)(double)(Double) map.get("size");
		}
		int id = (int)(double)(Double) map.get("id");
		return new ItemStack(id, metadata, quantity);
	}

	public static ForgeDirection stringToDirection(String argumentToCheck) {
		String[] directions = new String[] { "down", "up", "north", "south", "west", "east" };
		int i = 0;
		for (String dir : directions) {
			if (argumentToCheck.equals(directions)) {
				return ForgeDirection.getOrientation(i++);
			}
		}
		return ForgeDirection.UNKNOWN;
	}

}
