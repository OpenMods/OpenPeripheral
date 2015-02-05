package openperipheral.converter;

import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IInboundTypeConverter;

public class InboundTypeConverterAdapter extends GenericInboundConverterAdapter {

	private final IInboundTypeConverter converter;

	public InboundTypeConverterAdapter(IInboundTypeConverter converter) {
		this.converter = converter;
	}

	@Override
	protected Object toJava(IConverter registry, Object obj, Class<?> expected) {
		return converter.toJava(registry, obj, expected);
	}

}
