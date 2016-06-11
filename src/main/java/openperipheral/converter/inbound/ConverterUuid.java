package openperipheral.converter.inbound;

import java.util.UUID;
import openperipheral.api.converter.IConverter;
import openperipheral.converter.GenericInboundConverterAdapter;

public class ConverterUuid extends GenericInboundConverterAdapter {

	@Override
	public Object toJava(IConverter registry, Object obj, Class<?> expected) {
		if (expected == UUID.class && obj instanceof String) return UUID.fromString((String)obj);
		return null;
	}
}
