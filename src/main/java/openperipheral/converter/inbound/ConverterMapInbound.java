package openperipheral.converter.inbound;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IGenericInboundTypeConverter;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

public class ConverterMapInbound implements IGenericInboundTypeConverter {

	private static final TypeVariable<?> KEY;

	private static final TypeVariable<?> VALUE;

	static {
		TypeVariable<?>[] vars = Map.class.getTypeParameters();
		KEY = vars[0];
		VALUE = vars[1];
	}

	@Override
	public Object toJava(IConverter registry, Object obj, Type expected) {

		if (obj instanceof Map) {
			final TypeToken<?> type = TypeToken.of(expected);
			if (type.getRawType() == Map.class) {
				final Type keyType = type.resolveType(KEY).getType();
				final Type valueType = type.resolveType(VALUE).getType();

				Map<Object, Object> result = Maps.newHashMap();

				for (Map.Entry<?, ?> e : ((Map<?, ?>)obj).entrySet()) {
					Object key = registry.toJava(e.getKey(), keyType);
					Object value = registry.toJava(e.getValue(), valueType);
					result.put(key, value);
				}

				return result;
			}
		}

		return null;
	}

}
