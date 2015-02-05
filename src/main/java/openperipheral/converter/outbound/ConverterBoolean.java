package openperipheral.converter.outbound;

import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IOutboundTypeConverter;

// Need to handle boolean as default
public class ConverterBoolean implements IOutboundTypeConverter {

	@Override
	public Object fromJava(IConverter registry, Object obj) {
		return (obj instanceof Boolean)? obj : null;
	}

}
