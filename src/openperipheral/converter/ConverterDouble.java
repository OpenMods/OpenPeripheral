package openperipheral.converter;

import openperipheral.ITypeConverter;

public class ConverterDouble implements ITypeConverter {

	@Override
	public Object fromLua(Object o, Class required) {
		if ((required == Integer.class || required == int.class || required == byte.class)
				&& o instanceof Double) {
			Integer v = ((Double) o).intValue();
			if (required == byte.class) {
				return v.byteValue();
			}
			return (int) v;
		}
		return null;
	}

	@Override
	public Object toLua(Object o) {
		if (o instanceof Double) {
			return o;
		}
		return null;
	}

}
