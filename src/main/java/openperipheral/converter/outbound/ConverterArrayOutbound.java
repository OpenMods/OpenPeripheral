package openperipheral.converter.outbound;

import java.lang.reflect.Array;
import java.util.Map;

import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IOutboundTypeConverter;

import com.google.common.collect.Maps;

public class ConverterArrayOutbound implements IOutboundTypeConverter {

	@Override
	public Object fromJava(IConverter registry, Object o) {
		if (o.getClass().isArray()) {
			Map<Object, Object> ret = Maps.newHashMap();
			int length = Array.getLength(o);
			for (int i = 0; i < length; i++) {
				Object value = Array.get(o, i);
				ret.put(i + 1, registry.fromJava(value));
			}
			return ret;
		}
		return null;
	}

}
