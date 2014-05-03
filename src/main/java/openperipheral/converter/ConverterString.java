package openperipheral.converter;

import openperipheral.api.ITypeConverter;

public class ConverterString implements ITypeConverter {

	@Override
	public Object fromLua(Object obj, Class<?> expected) {
		if (expected == String.class) return obj.toString();

		return null;
	}

	@Override
	public Object toLua(Object obj) {
		return obj.toString(); // catch-all
	}

}
