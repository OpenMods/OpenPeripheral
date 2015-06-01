package openperipheral.converter.inbound;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import openmods.reflection.TypeUtils;
import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IGenericInboundTypeConverter;
import scala.actors.threadpool.Arrays;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

public class ConverterContainerInbound implements IGenericInboundTypeConverter {

	private final int offset;

	public ConverterContainerInbound(int offset) {
		this.offset = offset;
	}

	@Override
	public Object toJava(IConverter registry, Object obj, Type expected) {
		if (obj instanceof Map) {
			final TypeToken<?> type = TypeToken.of(expected);
			if (type.getRawType() == List.class) return convertToContainer(registry, obj, type);
		}

		return null;
	}

	protected Object convertToContainer(IConverter registry, Object obj, TypeToken<?> type) {
		final TypeToken<?> componentType = getComponentType(type);

		final Type valueType = componentType.getType();

		final Map<?, ?> m = (Map<?, ?>)obj;

		if (m.isEmpty()) return createEmptyContainer(componentType);

		int indexMax = Integer.MIN_VALUE;

		Map<Integer, Object> tmp = Maps.newHashMap();
		for (Map.Entry<?, ?> e : m.entrySet()) {
			final Object k = e.getKey();
			Preconditions.checkArgument(k instanceof Number, "Key '%s' is not number", k);
			int index = ((Number)k).intValue();
			Preconditions.checkArgument(index >= offset, "Indices must be larger than %s, got %s", offset, index);

			index -= offset;
			if (index > indexMax) indexMax = index;
			tmp.put(index, e.getValue());
		}

		final int size = indexMax + 1;
		Object[] result = new Object[size];

		for (int i = 0; i < size; i++) {
			Object o = tmp.get(i);
			if (o != null) {
				final Object converted = registry.toJava(o, valueType);
				result[i] = converted;
			}
		}

		return Arrays.asList(result);
	}

	protected ImmutableList<Object> createEmptyContainer(TypeToken<?> componentType) {
		return ImmutableList.of();
	}

	protected TypeToken<?> getComponentType(TypeToken<?> containerType) {
		return containerType.resolveType(TypeUtils.LIST_VALUE_PARAM);
	}

}
