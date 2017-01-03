package openperipheral.converter.inbound;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import openmods.reflection.TypeUtils;
import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IGenericInboundTypeConverter;

public class ConverterListInbound implements IGenericInboundTypeConverter {

	private final ContainerConverterHelper<Object[]> converter;

	private static final Object[] EMPTY = new Object[0];

	public ConverterListInbound(int offset) {
		this.converter = new ContainerConverterHelper<Object[]>(offset) {
			@Override
			protected TypeToken<?> getComponentType(TypeToken<?> containerType) {
				return containerType.resolveType(TypeUtils.LIST_VALUE_PARAM);
			}

			@Override
			protected Object[] createEmptyContainer(TypeToken<?> componentType) {
				return EMPTY;
			}

			@Override
			protected Object[] createNewContainer(TypeToken<?> componentType, int size) {
				return new Object[size];
			}

			@Override
			protected void setResult(Object[] result, int index, Object element) {
				result[index] = element;
			}
		};
	}

	@Override
	public Object toJava(IConverter registry, Object obj, Type expected) {
		if (obj instanceof Map) {
			final TypeToken<?> type = TypeToken.of(expected);
			if (type.getRawType() == List.class) {
				final Object[] elements = converter.convertToContainer(registry, obj, type);
				return Lists.newArrayList(elements);
			}
		}

		return null;
	}
}
