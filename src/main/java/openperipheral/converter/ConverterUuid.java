package openperipheral.converter;

import java.util.UUID;

import openperipheral.api.ITypeConverter;
import openperipheral.api.ITypeConvertersRegistry;

public class ConverterUuid implements ITypeConverter {

	@Override
	public Object fromLua(ITypeConvertersRegistry registry, Object obj, Class<?> expected) {
		if (expected == UUID.class && obj instanceof String) return UUID.fromString((String)obj);
		return null;
	}

	@Override
	public Object toLua(ITypeConvertersRegistry registry, Object obj) {
		if (obj instanceof UUID) return obj.toString();
		return null;
	}

}
