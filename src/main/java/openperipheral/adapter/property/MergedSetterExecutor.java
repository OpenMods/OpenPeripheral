package openperipheral.adapter.property;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import openperipheral.api.converter.IConverter;
import openperipheral.converter.TypeConverter;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Preconditions;

public class MergedSetterExecutor implements IPropertyExecutor {

	private final Field field;

	private final boolean singleNullable;

	private final IFieldManipulator singleManipulator;

	private final boolean indexedNullable;

	private final IIndexedFieldManipulator indexedManipulator;

	private final Type fieldValueType;

	private final Type keyType;

	private final IValueTypeProvider valueTypeProvider;

	public MergedSetterExecutor(Field field, boolean singleNullable, IFieldManipulator singleManipulator, boolean indexedNullable, IIndexedFieldManipulator indexedManipulator, Type keyType, IValueTypeProvider valueTypeProvider) {
		this.field = field;
		this.singleNullable = singleNullable;
		this.singleManipulator = singleManipulator;
		this.fieldValueType = field.getGenericType();
		this.indexedNullable = indexedNullable;
		this.indexedManipulator = indexedManipulator;
		this.keyType = keyType;
		this.valueTypeProvider = valueTypeProvider;
	}

	@Override
	public Object[] call(IConverter converter, Object target, Object... args) {

		if (args.length == 2) {
			final Object value = args[0];
			final Object key = args[1];

			final Object convertedKey = converter.toJava(key, keyType);
			Preconditions.checkArgument(convertedKey != null, "Failed to convert index to type %s", keyType);

			final Type valueType = valueTypeProvider.getType(convertedKey);
			final Object convertedValue = TypeConverter.nullableToJava(converter, indexedNullable, value, valueType);

			indexedManipulator.setField(target, field, convertedKey, convertedValue);

		} else if (args.length == 1) {
			final Object value = args[0];

			final Object converted = TypeConverter.nullableToJava(converter, singleNullable, value, fieldValueType);

			singleManipulator.setField(target, field, converted);

			return ArrayUtils.EMPTY_OBJECT_ARRAY;
		}

		return ArrayUtils.EMPTY_OBJECT_ARRAY;
	}

}
