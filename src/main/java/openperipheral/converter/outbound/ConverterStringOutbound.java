package openperipheral.converter.outbound;

import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IOutboundTypeConverter;

public class ConverterStringOutbound implements IOutboundTypeConverter {

	@Override
	public Object fromJava(IConverter registry, Object obj) {
		return obj.toString(); // catch-all
	}

}
