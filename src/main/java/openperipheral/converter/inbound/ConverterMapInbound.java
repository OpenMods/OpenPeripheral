package openperipheral.converter.inbound;

import java.lang.reflect.Type;
import java.util.Map;

import openmods.reflection.TypeUtils;
import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IGenericInboundTypeConverter;
import openperipheral.converter.TypeConverter;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

public class ConverterMapInbound implements IGenericInboundTypeConverter {

	@Override
	public Object toJava(IConverter registry, Object obj, Type expected) {

		if (obj instanceof Map) {
			final TypeToken<?> type = TypeToken.of(expected);
			if (type.getRawType() == Map.class) {
				final Type keyType = type.resolveType(TypeUtils.MAP_KEY_PARAM).getType();
				final Type valueType = type.resolveType(TypeUtils.MAP_VALUE_PARAM).getType();

				Map<Object, Object> result = Maps.newHashMap();

				for (Map.Entry<?, ?> e : ((Map<?, ?>)obj).entrySet()) {
					Object key = TypeConverter.nullableToJava(registry, e.getKey(), keyType);
					Object value = TypeConverter.nullableToJava(registry, e.getValue(), valueType);
					result.put(key, value);
				}

				return result;
			}
		}

		return null;
	}

}
