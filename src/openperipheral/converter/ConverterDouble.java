package openperipheral.converter;

import openperipheral.ITypeConverter;

public class ConverterDouble implements ITypeConverter {

	@Override
	public Object fromLua(Object o, Class required) {
		if ((required == Integer.class || required == int.class ||
				required == short.class || required == Short.class ||
				required == byte.class || required == Byte.class ||
				required == float.class || required == Float.class)
				&& o instanceof Double) {
			Integer v = ((Double) o).intValue();
			if (required == byte.class) {
				return v.byteValue();
			}
			if (required == short.class || required == Short.class) {
				return v.shortValue();
			}
			if (required == float.class || required == Float.class) {
				return ((Double) o).floatValue();
			}
			return (int) v;
		}
		return null;
	}

	@Override
	public Object toLua(Object o) {
		if (o instanceof Double || o instanceof Integer) {
			return o;
		}
		return null;
	}

}
