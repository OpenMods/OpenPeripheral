package openperipheral.converter.outbound;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import openperipheral.api.converter.IConverter;
import openperipheral.api.helpers.SimpleOutboundConverter;

public class ConverterListOutbound extends SimpleOutboundConverter<List<?>> {

	private final int offset;

	public ConverterListOutbound(int offset) {
		this.offset = offset;
	}

	@Override
	public Object convert(IConverter registry, List<?> list) {
		Map<Integer, Object> ret = Maps.newHashMap();

		for (int i = 0; i < list.size(); i++) {
			final Object value = list.get(i);
			final Object converted = registry.fromJava(value);
			ret.put(i + offset, converted);
		}

		return ret;
	}

}
