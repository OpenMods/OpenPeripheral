package openperipheral.converter;

import openmods.reflection.TypeUtils;
import openperipheral.api.converter.IConverter;

public class ConverterDefault extends GenericConverterAdapter {

	@Override
	public Object fromLua(IConverter registry, Object obj, Class<?> expected) {
		if (TypeUtils.compareTypes(obj.getClass(), expected)) return obj;
		return null;
	}

	@Override
	public Object toLua(IConverter registry, Object obj) {
		if (obj instanceof Boolean) return obj;

		return null;
	}

}
