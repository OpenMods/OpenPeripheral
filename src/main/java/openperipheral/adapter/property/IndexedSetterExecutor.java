package openperipheral.adapter.property;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import openperipheral.api.converter.IConverter;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Preconditions;

public class IndexedSetterExecutor implements IPropertyExecutor {

	private final Field field;

	private final IIndexedFieldManipulator manipulator;

	private final Type keyType;

	private final IValueTypeProvider valueTypeProvider;

	public IndexedSetterExecutor(Field field, IIndexedFieldManipulator manipulator, Type keyType, IValueTypeProvider valueTypeProvider) {
		this.field = field;
		this.manipulator = manipulator;
		this.keyType = keyType;
		this.valueTypeProvider = valueTypeProvider;
	}

	@Override
	public Object[] call(IConverter converter, Object target, Object... args) {
		Preconditions.checkArgument(args.length == 2, "Setter must have exactly two arguments (value and index)");
		final Object value = args[0];
		final Object key = args[1];

		final Object convertedKey = converter.toJava(key, keyType);
		Preconditions.checkArgument(convertedKey != null, "Failed to convert index to type %s", keyType);

		final Type valueType = valueTypeProvider.getType(convertedKey);
		final Object convertedValue = converter.toJava(value, valueType);
		Preconditions.checkArgument(convertedValue != null, "Failed to convert value to type %s", valueType);

		manipulator.setField(target, field, convertedKey, convertedValue);

		return ArrayUtils.EMPTY_OBJECT_ARRAY;
	}
}
