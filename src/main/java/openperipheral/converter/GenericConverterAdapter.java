package openperipheral.converter;

import java.lang.reflect.Type;

import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IGenericTypeConverter;

import com.google.common.reflect.TypeToken;

public abstract class GenericConverterAdapter implements IGenericTypeConverter {

	@Override
	public Object fromLua(IConverter registry, Object obj, Type expected) {
		TypeToken<?> type = TypeToken.of(expected);
		return fromLua(registry, obj, type.getRawType());
	}

	protected abstract Object fromLua(IConverter registry, Object obj, Class<?> expected);
}
