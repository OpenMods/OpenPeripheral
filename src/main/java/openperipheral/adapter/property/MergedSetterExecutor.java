package openperipheral.adapter.property;

import com.google.common.base.Preconditions;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import openperipheral.api.converter.IConverter;
import openperipheral.converter.TypeConverter;
import org.apache.commons.lang3.ArrayUtils;

public class MergedSetterExecutor implements IPropertyExecutor {

	private final Field field;

	private final boolean singleNullable;

	private final IFieldManipulator singleManipulator;

	private final SingleTypeInfo singleTypeInfo;

	private final ISinglePropertyAccessHandler singleAccessHandler;

	private final boolean indexedNullable;

	private final IIndexedFieldManipulator indexedManipulator;

	private final IndexedTypeInfo indexedTypeInfo;

	private final IIndexedPropertyAccessHandler indexedAccessHandler;

	public MergedSetterExecutor(Field field, boolean singleNullable, IFieldManipulator singleManipulator, SingleTypeInfo singleTypeInfo, ISinglePropertyAccessHandler singleAccessHandler, boolean indexedNullable, IIndexedFieldManipulator indexedManipulator, IndexedTypeInfo indexedTypeInfo, IIndexedPropertyAccessHandler indexedAccessHandler) {
		this.field = field;
		this.singleNullable = singleNullable;
		this.singleManipulator = singleManipulator;
		this.singleTypeInfo = singleTypeInfo;
		this.singleAccessHandler = singleAccessHandler;
		this.indexedNullable = indexedNullable;
		this.indexedManipulator = indexedManipulator;
		this.indexedTypeInfo = indexedTypeInfo;
		this.indexedAccessHandler = indexedAccessHandler;
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

			indexedAccessHandler.onSet(owner, target, field, convertedKey, convertedValue);
			indexedManipulator.setField(owner, target, field, convertedKey, convertedValue);

		} else if (args.length == 1) {
			final Object value = args[0];

			final Object target = PropertyUtils.getContents(owner, field);
			final Type valueType = singleTypeInfo.getValueType(target);
			final Object convertedValue = TypeConverter.nullableToJava(converter, singleNullable, value, valueType);

			singleAccessHandler.onSet(owner, target, field, convertedValue);
			singleManipulator.setField(owner, target, field, convertedValue);

			return ArrayUtils.EMPTY_OBJECT_ARRAY;
		} else {
			throw new IllegalArgumentException("This method must be called with one or two arguments");
		}

		return ArrayUtils.EMPTY_OBJECT_ARRAY;
	}

}
