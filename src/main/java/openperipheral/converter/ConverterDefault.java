package openperipheral.converter;

import openmods.reflection.ReflectionHelper;
import openperipheral.api.ITypeConverter;
import openperipheral.api.ITypeConvertersRegistry;

public class ConverterDefault implements ITypeConverter {

	@Override
	public Object fromLua(ITypeConvertersRegistry registry, Object obj, Class<?> expected) {
		if (ReflectionHelper.compareTypes(obj.getClass(), expected)) return obj;
		return null;
	}

	@Override
	public Object toLua(ITypeConvertersRegistry registry, Object obj) {
		if (obj instanceof Boolean) return obj;

		return null;
	}

}
