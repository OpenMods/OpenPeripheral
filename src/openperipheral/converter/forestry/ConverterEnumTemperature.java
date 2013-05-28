package openperipheral.converter.forestry;

import openperipheral.ITypeConverter;
import forestry.api.core.EnumTemperature;

public class ConverterEnumTemperature implements ITypeConverter {

	@Override
	public Object fromLua(Object o, Class required) {
		return null;
	}

	@Override
	public Object toLua(Object o) {
		if (o instanceof EnumTemperature) {
			return ((EnumTemperature)o).getName();
		}
		return null;
	}

}
