package openperipheral.converter;

import openperipheral.api.ITypeConverter;
import openperipheral.api.ITypeConvertersRegistry;

public class ConverterString implements ITypeConverter {

	@Override
	public Object fromLua(ITypeConvertersRegistry registry, Object obj, Class<?> expected) {
		if (expected == String.class) return obj.toString();

		return null;
	}

	@Override
	public Object toLua(ITypeConvertersRegistry registry, Object obj) {
		return obj.toString(); // catch-all
	}

}
