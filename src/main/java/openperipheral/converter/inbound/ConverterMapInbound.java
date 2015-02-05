package openperipheral.converter.inbound;

import java.util.Map;

import openperipheral.api.converter.IConverter;
import openperipheral.converter.GenericInboundConverterAdapter;

public class ConverterMapInbound extends GenericInboundConverterAdapter {

	@Override
	public Object toJava(IConverter registry, Object obj, Class<?> expected) {

		// TODO generic convert
		if (obj instanceof Map && expected == Map.class) return obj;

		return null;
	}

}
