package openperipheral.converter;

import java.util.HashMap;
import java.util.List;

import openperipheral.TypeConversionRegistry;
import openperipheral.api.ITypeConverter;

public class ConverterList implements ITypeConverter {

	@Override
	public Object fromLua(Object obj, Class<?> expected) {
		return null;
	}

	@Override
	public Object toLua(Object obj) {
		if (obj instanceof List) {
			HashMap<Object, Object> ret = new HashMap<Object, Object>();
			int index = 1;
			
			@SuppressWarnings("unchecked")
			List<Object> objList = (List<Object>)obj;
			
			for (int i = 0; i < objList.size(); i++) {
				ret.put(index++, TypeConversionRegistry.toLua(objList.get(i)));
			}
			return ret;
		}
		return null;
	}

}
