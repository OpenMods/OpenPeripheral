package openperipheral.converter;

import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.ForgeDirection;
import openperipheral.api.ITypeConverter;
import openperipheral.api.ITypeConvertersRegistry;

public class ConverterForgeDirection implements ITypeConverter {

	public static List<String> directions = Arrays.asList(new String[] { "down", "up", "north", "south", "west", "east" });

	@Override
	public Object fromLua(ITypeConvertersRegistry registry, Object o, Class<?> required) {
		if (required == ForgeDirection.class && o instanceof String) {
			for (int i = 0; i < directions.size(); i++) {
				if (directions.get(i).equals(o)) { return ForgeDirection.getOrientation(i); }
			}
			return ForgeDirection.UNKNOWN;
		}
		return null;
	}

	@Override
	public Object toLua(ITypeConvertersRegistry registry, Object o) {
		if (o instanceof ForgeDirection) { return directions.get(((ForgeDirection)o).ordinal()); }
		return null;
	}

}
