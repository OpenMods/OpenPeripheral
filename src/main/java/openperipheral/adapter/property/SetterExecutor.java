package openperipheral.adapter.property;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import openperipheral.api.converter.IConverter;
import openperipheral.converter.TypeConverter;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Preconditions;

public class SetterExecutor implements IPropertyExecutor {

	private final Field field;

	private final IFieldManipulator manipulator;

	private final SingleTypeInfo typeInfo;

	private final boolean nullable;

	public SetterExecutor(Field field, IFieldManipulator manipulator, SingleTypeInfo typeInfo, boolean nullable) {
		this.field = field;
		this.typeInfo = typeInfo;
		this.manipulator = manipulator;
		this.nullable = nullable;
	}

	@Override
	public Object[] call(IConverter converter, Object owner, Object... args) {
		Preconditions.checkArgument(args.length == 1, "Setter must have exactly one argument");
		final Object arg = args[0];

		final Object target = PropertyUtils.getContents(owner, field);
		final Type valueType = typeInfo.getValueType(target);
		final Object converted = TypeConverter.nullableToJava(converter, nullable, arg, valueType);
		manipulator.setField(owner, target, field, converted);

		return ArrayUtils.EMPTY_OBJECT_ARRAY;
	}
}