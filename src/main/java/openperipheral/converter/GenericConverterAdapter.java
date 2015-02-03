package openperipheral.converter;

import java.lang.reflect.Type;

import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IGenericTypeConverter;

public abstract class GenericConverterAdapter implements IGenericTypeConverter {

	@Override
	public Object fromLua(IConverter registry, Object obj, Type expected) {
		if (expected instanceof Class) return fromLua(registry, obj, (Class<?>)expected);

		return null;
	}

	protected abstract Object fromLua(IConverter registry, Object obj, Class<?> expected);
}
