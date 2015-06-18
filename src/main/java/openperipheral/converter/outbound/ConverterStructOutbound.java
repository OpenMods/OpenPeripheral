package openperipheral.converter.outbound;

import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IOutboundTypeConverter;
import openperipheral.converter.StructCache;
import openperipheral.converter.StructCache.IStructHandler;

public class ConverterStructOutbound implements IOutboundTypeConverter {

	private final int indexOffset;

	public ConverterStructOutbound(int indexOffset) {
		this.indexOffset = indexOffset;
	}

	@Override
	public Object fromJava(IConverter converter, Object obj) {
		final Class<?> cls = obj.getClass();

		if (StructCache.instance.isStruct(cls)) {
			final IStructHandler structConverter = StructCache.instance.getHandler(cls);
			return structConverter.fromJava(converter, obj, indexOffset);
		}

		return null;
	}

}
