package openperipheral.converter;

import java.util.Map;
import java.util.Set;

import openperipheral.api.ITypeConverter;
import openperipheral.api.ITypeConvertersRegistry;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ConverterSet implements ITypeConverter {

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object fromLua(ITypeConvertersRegistry registry, Object obj, Class<?> expected) {
		if (obj instanceof Map && expected == Set.class) { return Sets.newHashSet(((Map)obj).keySet()); }

		return null;
	}

	@Override
	@SuppressWarnings({ "rawtypes" })
	public Object toLua(ITypeConvertersRegistry registry, Object obj) {
		if (obj instanceof Set) {
			Map<Object, Boolean> result = Maps.newHashMap();
			for (Object e : (Set)obj) {
				result.put(registry.toLua(e), true);
			}
			return result;
		}
		return null;
	}

}
