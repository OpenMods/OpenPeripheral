package openperipheral.converter;

import java.util.HashMap;

import openperipheral.ITypeConverter;
import openperipheral.TypeConversionRegistry;

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
			Object[] objArray = (Object[])o;
			for (int i = 0; i < objArray.length; i++) {
				ret.put(i++, TypeConversionRegistry.toLua(objArray[i]));
			}
			return ret;
		}
		return null;
	}

}
