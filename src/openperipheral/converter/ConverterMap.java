package openperipheral.converter;

import java.util.Map;

import openperipheral.TypeConversionRegistry;
import openperipheral.api.ITypeConverter;

import com.google.common.collect.Maps;

public class ConverterMap implements ITypeConverter {

	@Override
	public Object fromLua(Object obj, Class<?> expected) {
		if (obj instanceof Map && expected == Map.class) return obj;

		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object toLua(Object obj) {
		if (obj instanceof Map) {
			Map<Object, Object> transformed = Maps.newHashMap();
			for (Map.Entry<Object, Object> e : ((Map<Object, Object>)obj).entrySet()) {
				Object k = TypeConversionRegistry.toLua(e.getKey());
				Object v = TypeConversionRegistry.toLua(e.getValue());
				transformed.put(k, v);
			}
			return transformed;
		}

		return null;
	}

}
