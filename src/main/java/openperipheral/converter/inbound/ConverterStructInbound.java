package openperipheral.converter.inbound;

import java.util.Map;
import openperipheral.api.converter.IConverter;
import openperipheral.converter.GenericInboundConverterAdapter;
import openperipheral.converter.StructHandlerProvider;
import openperipheral.converter.StructHandlerProvider.IStructHandler;

public class ConverterStructInbound extends GenericInboundConverterAdapter {

	private final int indexOffset;

	public ConverterStructInbound(int indexOffset) {
		this.indexOffset = indexOffset;
	}

	@Override
	protected Object toJava(IConverter converter, Object obj, Class<?> expected) {
		if (obj instanceof Map && StructHandlerProvider.instance.isStruct(expected)) {
			final Map<?, ?> data = (Map<?, ?>)obj;
			final IStructHandler structConverter = StructHandlerProvider.instance.getHandler(expected);
			return structConverter.toJava(converter, data, indexOffset);
		}

		return null;
	}

}
