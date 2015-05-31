package openperipheral.adapter.property;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import openperipheral.api.converter.IConverter;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Preconditions;

public class SetterExecutor implements IPropertyExecutor {

	private final Field field;

	private final IFieldManipulator manipulator;

	private final Type expectedType;

	public SetterExecutor(Field field, IFieldManipulator manipulator) {
		this.field = field;
		this.expectedType = field.getGenericType();
		this.manipulator = manipulator;
	}

	@Override
	public Object[] call(IConverter converter, Object target, Object... args) {
		Preconditions.checkArgument(args.length == 1, "Setter must have exactly one argument");
		final Object arg = args[0];

		final Object converted = converter.toJava(arg, expectedType);
		Preconditions.checkArgument(converted != null, "Failed to convert to type %s", expectedType);

		manipulator.setField(target, field, converted);

		return ArrayUtils.EMPTY_OBJECT_ARRAY;
	}
}