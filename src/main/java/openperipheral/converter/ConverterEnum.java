package openperipheral.converter;

import java.util.Arrays;

import openperipheral.api.ITypeConverter;
import openperipheral.api.ITypeConvertersRegistry;

public class ConverterEnum implements ITypeConverter {
	@Override
	public Object fromLua(ITypeConvertersRegistry registry, Object obj, Class<?> expected) {
		if (expected.isEnum() && obj instanceof String) {
			String value = (String)obj;
			Object[] constants = expected.getEnumConstants();
			for (Object o : constants) {
				if (o.toString().equalsIgnoreCase(value)) return o;
			}
			throw new IllegalArgumentException(String.format("'%s' is not valid enum value, must be %s", value, Arrays.toString(constants)));
		}

		return null;
	}

	@Override
	public Object toLua(ITypeConvertersRegistry registry, Object obj) {
		if (obj instanceof Enum) return obj.toString();
		return null;
	}
}
