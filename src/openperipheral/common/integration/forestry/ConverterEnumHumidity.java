package openperipheral.common.integration.forestry;

import openperipheral.api.ITypeConverter;
import forestry.api.core.EnumHumidity;

public class ConverterEnumHumidity implements ITypeConverter {

	@Override
	public Object fromLua(Object o, Class required) {
		return null;
	}

	@Override
	public Object toLua(Object o) {
		if (o instanceof EnumHumidity) {
			return ((EnumHumidity) o).getName();
		}
		return null;
	}

}
