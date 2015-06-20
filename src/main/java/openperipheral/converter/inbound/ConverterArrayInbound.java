package openperipheral.converter.inbound;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Map;

import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IGenericInboundTypeConverter;

import com.google.common.reflect.TypeToken;

public class ConverterArrayInbound implements IGenericInboundTypeConverter {

	private final ContainerConverterHelper<Object> converter;

	public ConverterArrayInbound(int offset) {
		this.converter = new ContainerConverterHelper<Object>(offset) {

			@Override
			protected TypeToken<?> getComponentType(TypeToken<?> containerType) {
				return containerType.getComponentType();
			}

			@Override
			protected Object createEmptyContainer(TypeToken<?> componentType) {
				return Array.newInstance(componentType.getRawType(), 0);
			}

			@Override
			protected Object createNewContainer(TypeToken<?> componentType, int size) {
				return Array.newInstance(componentType.getRawType(), size);
			}

			@Override
			protected void setResult(Object result, int index, Object element) {
				Array.set(result, index, element);
			}
		};
	}

	@Override
	public Object toJava(IConverter registry, Object o, Type required) {
		if (o instanceof Map) {
			final TypeToken<?> type = TypeToken.of(required);
			if (type.isArray()) return converter.convertToContainer(registry, o, type);
		}

		return null;
	}

}
