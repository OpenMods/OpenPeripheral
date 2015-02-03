package openperipheral.converter;

import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.ITypeConverter;

public class TypeConverterAdapter extends GenericConverterAdapter {

	private final ITypeConverter converter;

	public TypeConverterAdapter(ITypeConverter converter) {
		this.converter = converter;
	}

	@Override
	public Object toLua(IConverter registry, Object obj) {
		return converter.toLua(registry, obj);
	}

	@Override
	protected Object fromLua(IConverter registry, Object obj, Class<?> expected) {
		return converter.fromLua(registry, obj, expected);
	}

}
