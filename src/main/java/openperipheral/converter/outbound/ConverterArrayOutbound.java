package openperipheral.converter.outbound;

import java.lang.reflect.Array;
import java.util.Map;

import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IOutboundTypeConverter;

import com.google.common.collect.Maps;

public class ConverterArrayOutbound implements IOutboundTypeConverter {

	private final int offset;

	public ConverterArrayOutbound(int offset) {
		this.offset = offset;
	}

	@Override
	public Object fromJava(IConverter registry, Object o) {
		if (o.getClass().isArray()) {
			Map<Object, Object> ret = Maps.newHashMap();
			int length = Array.getLength(o);
			for (int i = 0; i < length; i++) {
				final Object value = Array.get(o, i);
				final Object converted = registry.fromJava(value);
				ret.put(i + offset, converted);
			}
			return ret;
		}
		return null;
	}

}
