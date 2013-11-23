package openperipheral.core.converter;

import java.util.HashMap;

import openperipheral.api.ITypeConverter;
import openperipheral.core.TypeConversionRegistry;

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
			for (int i = 0; i < objArray.length; i++) {
				ret.put(index++, TypeConversionRegistry.toLua(objArray[i]));
			}
			return ret;
		}
		return null;
	}

}
