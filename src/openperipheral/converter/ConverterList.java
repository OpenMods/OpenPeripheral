package openperipheral.converter;

import java.util.List;
import java.util.Map;

import openperipheral.TypeConversionRegistry;
import openperipheral.api.ITypeConverter;

import com.google.common.collect.Maps;

public class ConverterList implements ITypeConverter {

	@Override
	public Object fromLua(Object obj, Class<?> expected) {
		return null;
	}

	@Override
	public Object toLua(Object obj) {
		if (obj instanceof List) {
			Map<Integer, Object> ret = Maps.newHashMap();

			@SuppressWarnings("unchecked")
			List<Object> objList = (List<Object>)obj;

			for (int i = 0; i < objList.size(); i++) {
				ret.put(i + 1, TypeConversionRegistry.toLua(objList.get(i)));
			}
			return ret;
		}
		return null;
	}

}
