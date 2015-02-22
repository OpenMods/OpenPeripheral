package openperipheral.converter.inbound;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Map;

import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IGenericInboundTypeConverter;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

public class ConverterArrayInbound implements IGenericInboundTypeConverter {

	@Override
	public Object toJava(IConverter registry, Object o, Type required) {
		if (o instanceof Map) {
			final TypeToken<?> type = TypeToken.of(required);
			if (type.isArray()) {
				@SuppressWarnings("unchecked")
				Map<Object, Object> m = (Map<Object, Object>)o;

				final TypeToken<?> component = type.getComponentType();

				final Class<?> rawComponent = component.getRawType();

				final Type genericComponent = component.getType();

				if (m.isEmpty()) return Array.newInstance(rawComponent, 0);

				int indexMin = Integer.MAX_VALUE;
				int indexMax = Integer.MIN_VALUE;

				Map<Integer, Object> tmp = Maps.newHashMap();
				for (Map.Entry<Object, Object> e : m.entrySet()) {
					Object k = e.getKey();
					if (!(k instanceof Number)) return null;
					int index = ((Number)k).intValue();
					if (index < indexMin) indexMin = index;
					if (index > indexMax) indexMax = index;
					tmp.put(index, e.getValue());
				}

				int size = indexMax - indexMin + 1;
				if (indexMin != 0 && indexMin != 1) return null;

				Object result = Array.newInstance(rawComponent, size);
				for (int i = 0, index = indexMin; i < size; i++, index++) {
					Object in = tmp.get(index);
					if (in == null) continue;
					Object out = registry.toJava(in, genericComponent);
					Array.set(result, i, out);
				}

				return result;
			}
		}

		return null;
	}

}
