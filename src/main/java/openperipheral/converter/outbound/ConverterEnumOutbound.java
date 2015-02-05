package openperipheral.converter.outbound;

import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IOutboundTypeConverter;

public class ConverterEnumOutbound implements IOutboundTypeConverter {

	@Override
	public Object fromJava(IConverter converter, Object obj) {
		return (obj instanceof Enum)? obj.toString().toLowerCase() : null;
	}

}
