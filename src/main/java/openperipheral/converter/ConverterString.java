package openperipheral.converter;

import openperipheral.api.converter.IConverter;

public class ConverterString extends GenericConverterAdapter {

	@Override
	public Object fromLua(IConverter registry, Object obj, Class<?> expected) {
		if (expected == String.class) return obj.toString();

		return null;
	}

	@Override
	public Object toLua(IConverter registry, Object obj) {
		return obj.toString(); // catch-all
	}

}
