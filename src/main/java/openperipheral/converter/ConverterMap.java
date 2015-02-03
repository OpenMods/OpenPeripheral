package openperipheral.converter;

import java.util.Map;

import openperipheral.api.converter.IConverter;

import com.google.common.collect.Maps;

public class ConverterMap extends GenericConverterAdapter {

	@Override
	public Object fromLua(IConverter registry, Object obj, Class<?> expected) {
		if (obj instanceof Map && expected == Map.class) return obj;

		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object toLua(IConverter registry, Object obj) {
		if (obj instanceof Map) {
			Map<Object, Object> transformed = Maps.newHashMap();
			for (Map.Entry<Object, Object> e : ((Map<Object, Object>)obj).entrySet()) {
				Object k = registry.toLua(e.getKey());
				Object v = registry.toLua(e.getValue());
				transformed.put(k, v);
			}
			return transformed;
		}

		return null;
	}

}
