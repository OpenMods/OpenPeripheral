package openperipheral.converter;

import openperipheral.api.ITypeConverter;

public class ConverterDouble implements ITypeConverter {

	@Override
	public Object fromLua(Object o, Class<?> required) {
		if (o instanceof Double) {
			final Double d = (Double)o;

			if (required == Integer.class || required == int.class) return d.intValue();

			if (required == Double.class || required == double.class) return d;

			if (required == Float.class || required == float.class) return d.floatValue();

			if (required == Long.class || required == long.class) return d.longValue();

			if (required == Short.class || required == short.class) return d.shortValue();

			if (required == Byte.class || required == byte.class) return d.byteValue();

			if (required == Boolean.class || required == boolean.class) return d != 0;
		}

		return null;
	}

	@Override
	public Object toLua(Object o) {
		return (o instanceof Number)? ((Number)o).doubleValue() : null;
	}

}
