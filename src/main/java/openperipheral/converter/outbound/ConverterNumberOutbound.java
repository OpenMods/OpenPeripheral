package openperipheral.converter.outbound;

import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IOutboundTypeConverter;

public class ConverterNumberOutbound implements IOutboundTypeConverter {

	@Override
	public Object fromJava(IConverter registry, Object o) {
		return (o instanceof Number)? ((Number)o).doubleValue() : null;
	}

}
