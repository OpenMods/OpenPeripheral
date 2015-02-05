package openperipheral.converter.outbound;

import java.util.Map;

import openperipheral.api.converter.IConverter;
import openperipheral.api.helpers.SimpleOutboundConverter;

import com.google.common.collect.Maps;

public class ConverterMapOutbound extends SimpleOutboundConverter<Map<?, ?>> {

	@Override
	public Object convert(IConverter registry, Map<?, ?> obj) {
		Map<Object, Object> transformed = Maps.newHashMap();
		for (Map.Entry<?, ?> e : obj.entrySet()) {
			Object k = registry.fromJava(e.getKey());
			Object v = registry.fromJava(e.getValue());
			transformed.put(k, v);
		}
		return transformed;
	}

}
