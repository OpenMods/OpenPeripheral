package openperipheral.converter.outbound;

import java.util.Map;
import java.util.Set;

import openperipheral.api.converter.IConverter;
import openperipheral.api.helpers.SimpleOutboundConverter;

import com.google.common.collect.Maps;

public class ConverterSetOutbound extends SimpleOutboundConverter<Set<?>> {

	@Override
	public Object convert(IConverter registry, Set<?> obj) {
		Map<Object, Boolean> result = Maps.newHashMap();
		for (Object e : obj) {
			result.put(registry.fromJava(e), true);
		}
		return result;
	}

}
