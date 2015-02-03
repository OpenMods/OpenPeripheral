package openperipheral.converter;

import java.util.UUID;

import openperipheral.api.converter.IConverter;

public class ConverterUuid extends GenericConverterAdapter {

	@Override
	public Object fromLua(IConverter registry, Object obj, Class<?> expected) {
		if (expected == UUID.class && obj instanceof String) return UUID.fromString((String)obj);
		return null;
	}

	@Override
	public Object toLua(IConverter registry, Object obj) {
		if (obj instanceof UUID) return obj.toString();
		return null;
	}

}
