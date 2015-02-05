package openperipheral.converter.inbound;

import java.util.List;
import java.util.Map;

import openperipheral.api.converter.IConverter;
import openperipheral.converter.GenericInboundConverterAdapter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ConverterListInbound extends GenericInboundConverterAdapter {

	@Override
	public Object toJava(IConverter registry, Object obj, Class<?> expected) {
		if (obj instanceof Map && expected == List.class) {
			@SuppressWarnings("unchecked")
			Map<Object, Object> m = (Map<Object, Object>)obj;

			if (m.isEmpty()) return ImmutableList.of();

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
			if (size != tmp.size() || (indexMin != 0 && indexMin != 1)) return null;

			List<Object> result = Lists.newArrayList();

			for (int index = indexMin; index <= indexMax; index++) {
				Object o = tmp.get(index);
				result.add(o);
			}

			return result;
		}

		return null;
	}

}
