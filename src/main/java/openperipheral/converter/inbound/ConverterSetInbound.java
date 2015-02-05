package openperipheral.converter.inbound;

import java.util.Map;
import java.util.Set;

import openperipheral.api.converter.IConverter;
import openperipheral.converter.GenericInboundConverterAdapter;

import com.google.common.collect.Sets;

public class ConverterSetInbound extends GenericInboundConverterAdapter {

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object toJava(IConverter registry, Object obj, Class<?> expected) {
		// TODO generic convert

		if (obj instanceof Map && expected == Set.class) { return Sets.newHashSet(((Map)obj).keySet()); }

		return null;
	}

}
