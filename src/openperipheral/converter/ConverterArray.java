package openperipheral.converter;

import java.lang.reflect.Array;
import java.util.Map;

import openperipheral.TypeConversionRegistry;
import openperipheral.api.ITypeConverter;

import com.google.common.collect.Maps;

public class ConverterArray implements ITypeConverter {

	@Override
	public Object fromLua(Object o, Class<?> required) {
		if (o instanceof Map && required.isArray()) {
			@SuppressWarnings("unchecked")
			Map<Object, Object> m = (Map<Object, Object>)o;

			Class<?> component = required.getComponentType();

			if (m.isEmpty()) return Array.newInstance(component, 0);

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

			Object result = Array.newInstance(component, size);
			for (int i = 0, index = indexMin; i < size; i++, index++) {
				Object in = tmp.get(index);
				Object out = TypeConversionRegistry.fromLua(in, component);
				if (out == null) return null;
				Array.set(result, i, out);
			}

			return result;
		}

		return null;
	}

	@Override
	public Object toLua(Object o) {
		if (o.getClass().isArray()) {
			Map<Object, Object> ret = Maps.newHashMap();
			int index = 1;
			Object[] objArray = (Object[])o;
			for (Object element : objArray) {
				ret.put(index++, TypeConversionRegistry.toLua(element));
			}
			return ret;
		}
		return null;
	}

}
