package openperipheral.converter.inbound;

import openperipheral.api.converter.IConverter;
import openperipheral.converter.GenericInboundConverterAdapter;

public class ConverterRawInbound extends GenericInboundConverterAdapter {

	@Override
	protected Object toJava(IConverter converter, Object obj, Class<?> expected) {
		if (expected == Object.class) return obj;
		return null;
	}

}
