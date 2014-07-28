package openperipheral.converter;

import openperipheral.ApiImplementation;
import openperipheral.TypeConversionRegistry;
import openperipheral.api.ITypeConverter;
import openperipheral.api.ITypeConvertersRegistry;

@ApiImplementation
public class TypeConvertersRegistryWrapper implements ITypeConvertersRegistry {

	@Override
	public void register(ITypeConverter converter) {
		TypeConversionRegistry.registerTypeConverter(converter);
	}

	@Override
	public Object fromLua(Object obj, Class<?> expected) {
		return TypeConversionRegistry.fromLua(obj, expected);
	}

	@Override
	public Object toLua(Object obj) {
		return TypeConversionRegistry.toLua(obj);
	}

}