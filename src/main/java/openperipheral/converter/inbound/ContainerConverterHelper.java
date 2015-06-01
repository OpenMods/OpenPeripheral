package openperipheral.converter.inbound;

import java.lang.reflect.Type;
import java.util.Map;

import openperipheral.api.converter.IConverter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

public abstract class ContainerConverterHelper<R> {
	private final int offset;

	public ContainerConverterHelper(int offset) {
		this.offset = offset;
	}

	public R convertToContainer(IConverter registry, Object obj, TypeToken<?> type) {
		final TypeToken<?> componentType = getComponentType(type);

		final Type valueType = componentType.getType();

		final Map<?, ?> m = (Map<?, ?>)obj;

		if (m.isEmpty()) return createEmptyContainer(componentType);

		int indexMax = Integer.MIN_VALUE;

		final Map<Integer, Object> tmp = Maps.newHashMap();
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
		R result = createNewContainer(componentType, size);

		for (int i = 0; i < size; i++) {
			Object o = tmp.get(i);
			if (o != null) {
				final Object converted = registry.toJava(o, valueType);
				setResult(result, i, converted);
			}
		}

		return result;
	}

	protected abstract TypeToken<?> getComponentType(TypeToken<?> containerType);

	protected abstract R createEmptyContainer(TypeToken<?> componentType);

	protected abstract R createNewContainer(TypeToken<?> componentType, int size);

	protected abstract void setResult(R result, int index, Object element);

}
