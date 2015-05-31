package openperipheral.converter.inbound;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import openmods.reflection.TypeUtils;
import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IGenericInboundTypeConverter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

public class ConverterListInbound implements IGenericInboundTypeConverter {

	@Override
	public Object toJava(IConverter registry, Object obj, Type expected) {
		if (obj instanceof Map) {
			final TypeToken<?> type = TypeToken.of(expected);
			if (type.getRawType() == List.class) {
				final Type valueType = type.resolveType(TypeUtils.LIST_VALUE_PARAM).getType();

				final Map<?, ?> m = (Map<?, ?>)obj;

				if (m.isEmpty()) return ImmutableList.of();

				int indexMin = Integer.MAX_VALUE;
				int indexMax = Integer.MIN_VALUE;

				Map<Integer, Object> tmp = Maps.newHashMap();
				for (Map.Entry<?, ?> e : m.entrySet()) {
					Object k = e.getKey();
					if (!(k instanceof Number)) return null;
					int index = ((Number)k).intValue();
					if (index < indexMin) indexMin = index;
					if (index > indexMax) indexMax = index;
					tmp.put(index, e.getValue());
				}

				if (indexMin != 0 && indexMin != 1) return null;

				List<Object> result = Lists.newArrayList();

				for (int index = indexMin; index <= indexMax; index++) {
					Object o = tmp.get(index);
					final Object converted = registry.toJava(o, valueType);
					result.add(converted);
				}

				return result;
			}
		}

		return null;
	}

}
