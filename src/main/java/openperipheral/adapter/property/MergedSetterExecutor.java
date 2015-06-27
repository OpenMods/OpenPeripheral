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

	private final SingleTypeInfo singleTypeInfo;

	private final boolean indexedNullable;

	private final IIndexedFieldManipulator indexedManipulator;

	private final IndexedTypeInfo indexedTypeInfo;

	public MergedSetterExecutor(Field field, boolean singleNullable, IFieldManipulator singleManipulator, SingleTypeInfo singleTypeInfo, boolean indexedNullable, IIndexedFieldManipulator indexedManipulator, IndexedTypeInfo indexedTypeInfo) {
		this.field = field;
		this.singleNullable = singleNullable;
		this.singleManipulator = singleManipulator;
		this.singleTypeInfo = singleTypeInfo;
		this.indexedNullable = indexedNullable;
		this.indexedManipulator = indexedManipulator;
		this.indexedTypeInfo = indexedTypeInfo;
	}

	@Override
	public Object[] call(IConverter converter, Object owner, Object... args) {
		if (args.length == 2) {
			final Object value = args[0];
			final Object key = args[1];

			final Type keyType = indexedTypeInfo.keyType;
			final Object convertedKey = converter.toJava(key, keyType);
			Preconditions.checkArgument(convertedKey != null, "Failed to convert index to type %s", keyType);

			final Object target = PropertyUtils.getContents(owner, field);
			final Type valueType = indexedTypeInfo.getValueType(target, convertedKey);
			final Object convertedValue = TypeConverter.nullableToJava(converter, indexedNullable, value, valueType);

			indexedManipulator.setField(owner, target, field, convertedKey, convertedValue);

		} else if (args.length == 1) {
			final Object value = args[0];

			final Object target = PropertyUtils.getContents(owner, field);
			final Type valueType = singleTypeInfo.getValueType(target);
			final Object convertedValue = TypeConverter.nullableToJava(converter, singleNullable, value, valueType);

			singleManipulator.setField(owner, target, field, convertedValue);

			return ArrayUtils.EMPTY_OBJECT_ARRAY;
		} else {
			throw new IllegalArgumentException("This method must be called with one or two arguments");
		}

		return ArrayUtils.EMPTY_OBJECT_ARRAY;
	}

}
