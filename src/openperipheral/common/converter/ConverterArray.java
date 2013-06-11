package openperipheral.common.converter;

import java.util.HashMap;

import openperipheral.api.ITypeConverter;

public class ConverterArray implements ITypeConverter {

	@Override
	public Object fromLua(Object o, Class required) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object toLua(Object o) {
		if (o.getClass().isArray()) {
			HashMap ret = new HashMap();
			int index = 1;
			Object[] objArray = (Object[]) o;
			for (int i = 0; i < objArray.length; i++) {
				ret.put(index++, TypeConversionRegistry.toLua(objArray[i]));
			}
			return ret;
		}
		return null;
	}

}
