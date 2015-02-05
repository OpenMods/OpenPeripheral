package openperipheral.converter.inbound;

import openmods.reflection.TypeUtils;
import openperipheral.api.converter.IConverter;
import openperipheral.converter.GenericInboundConverterAdapter;

public class ConverterBypass extends GenericInboundConverterAdapter {

	@Override
	protected Object toJava(IConverter converter, Object obj, Class<?> expected) {
		if (TypeUtils.compareTypes(obj.getClass(), expected)) return obj;
		return null;
	}

}
