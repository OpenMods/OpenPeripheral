package openperipheral.converter.inbound;

import java.util.Arrays;

import openperipheral.api.converter.IConverter;
import openperipheral.converter.GenericInboundConverterAdapter;

public class ConverterEnumInbound extends GenericInboundConverterAdapter {
	@Override
	public Object toJava(IConverter registry, Object obj, Class<?> expected) {
		if (expected.isEnum()) {
			Object[] constants = expected.getEnumConstants();
			if (obj instanceof String) {
				String value = (String)obj;
				for (Object o : constants) {
					if (o.toString().equalsIgnoreCase(value)) return o;
				}
			}
			throw new IllegalArgumentException(String.format("'%s' is not valid enum value, must be %s", obj, Arrays.toString(constants)));
		}

		return null;
	}

}
