package openperipheral.adapter.property;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import openperipheral.api.converter.IConverter;
import openperipheral.converter.TypeConverter;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Preconditions;

public class IndexedSetterExecutor implements IPropertyExecutor {

	private final Field field;

	private final IIndexedFieldManipulator manipulator;

	private final IndexedTypeInfo typeInfo;

	private final IIndexedPropertyAccessHandler accessHandler;

	private final boolean nullable;

	public IndexedSetterExecutor(Field field, IIndexedFieldManipulator manipulator, IndexedTypeInfo typeInfo, IIndexedPropertyAccessHandler accessHandler, boolean nullable) {
		this.field = field;
		this.manipulator = manipulator;
		this.typeInfo = typeInfo;
		this.accessHandler = accessHandler;
		this.nullable = nullable;
	}

	@Override
	public Object[] call(IConverter converter, Object owner, Object... args) {
		Preconditions.checkArgument(args.length == 2, "Setter must have exactly two arguments (value and index)");
		final Object value = args[0];
		final Object key = args[1];

		final Type keyType = typeInfo.keyType;
		final Object convertedKey = converter.toJava(key, keyType);
		Preconditions.checkArgument(convertedKey != null, "Failed to convert index to type %s", keyType);

		final Object target = PropertyUtils.getContents(owner, field);

		final Type valueType = typeInfo.getValueType(target, convertedKey);
		final Object convertedValue = TypeConverter.nullableToJava(converter, nullable, value, valueType);

		accessHandler.onSet(owner, target, field, convertedKey, convertedValue);
		manipulator.setField(owner, target, field, convertedKey, convertedValue);

		return ArrayUtils.EMPTY_OBJECT_ARRAY;
	}
}
