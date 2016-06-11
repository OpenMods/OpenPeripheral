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
			} else if (obj instanceof Number) {
				int value = ((Number)obj).intValue() - 1;
				try {
					return constants[value];
				} catch (IndexOutOfBoundsException e) {
					// ignore, will fail anyway with more descriptive exception
				}
			}
			throw new IllegalArgumentException(String.format("'%s' is not valid enum value, must be %s or 1..%d", obj, Arrays.toString(constants), constants.length));
		}

		return null;
	}

}
