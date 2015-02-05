package openperipheral.converter.inbound;

import java.lang.reflect.Type;

import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IGenericInboundTypeConverter;

public class ConverterStringInbound implements IGenericInboundTypeConverter {

	@Override
	public Object toJava(IConverter converter, Object obj, Type expected) {
		return (expected == String.class)? String.valueOf(obj) : null;
	}
}
