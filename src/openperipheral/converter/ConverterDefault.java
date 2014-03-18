package openperipheral.converter;

import openmods.utils.ReflectionHelper;
import openperipheral.api.ITypeConverter;

public class ConverterDefault implements ITypeConverter {

	@Override
	public Object fromLua(Object obj, Class<?> expected) {
		if (ReflectionHelper.compareTypes(obj.getClass(), expected)) return obj;
		return null;
	}

	@Override
	public Object toLua(Object obj) {
		if (obj instanceof Boolean) return obj;

		return null;
	}

}
