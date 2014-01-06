package openperipheral.converter;

import java.util.HashMap;

import openperipheral.TypeConversionRegistry;
import openperipheral.api.ITypeConverter;

public class ConverterArray implements ITypeConverter {

	@Override
	public Object fromLua(Object o, Class<?> required) {
		return null;
	}

	@Override
	public Object toLua(Object o) {
		if (o.getClass().isArray()) {
			HashMap<Object, Object> ret = new HashMap<Object, Object>();
			int index = 1;
			Object[] objArray = (Object[])o;
			for (Object element : objArray) {
				ret.put(index++, TypeConversionRegistry.toLua(element));
			}
			return ret;
		}
		return null;
	}

}
