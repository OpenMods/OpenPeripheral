package openperipheral.converter;

import openperipheral.ITypeConverter;

public class ConverterDouble implements ITypeConverter {

	@Override
	public Object fromLua(Object o, Class required) {
		if (required == int.class && o instanceof Double){
			return ((Double)o).intValue();
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
