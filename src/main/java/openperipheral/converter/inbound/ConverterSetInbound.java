package openperipheral.converter.inbound;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IGenericInboundTypeConverter;
import openperipheral.converter.TypeConverter;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;

public class ConverterSetInbound implements IGenericInboundTypeConverter {

	private static final TypeVariable<?> TYPE_PARAM = Set.class.getTypeParameters()[0];

	private static boolean isTruthish(Object v) {
		if (v == null) return false;
		if (v instanceof Boolean) return (Boolean)v;
		if (v instanceof Number) return ((Number)v).doubleValue() != 0;
		if (v instanceof String) return !Strings.isNullOrEmpty((String)v);
		if (v instanceof Map) return !((Map<?, ?>)v).isEmpty();
		if (v instanceof Collection) return !((Collection<?>)v).isEmpty();

		return false;
	}

	@Override
	public Object toJava(IConverter registry, Object obj, Type expected) {
		if (obj instanceof Map) {
			final TypeToken<?> type = TypeToken.of(expected);
			if (type.getRawType() == Set.class) {
				final Type valueType = type.resolveType(TYPE_PARAM).getType();

				Set<Object> result = Sets.newHashSet();

				for (Map.Entry<?, ?> e : ((Map<?, ?>)obj).entrySet()) {
					final Object value = e.getKey();

					Object marker = e.getValue();
					if (isTruthish(marker)) {
						Object converted = TypeConverter.nullableToJava(registry, value, valueType);
						result.add(converted);
					}
				}

				return result;
			}
		}

		return null;
	}

}
