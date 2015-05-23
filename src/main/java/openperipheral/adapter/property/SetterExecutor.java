package openperipheral.adapter.property;

import java.lang.reflect.Field;

import openperipheral.api.converter.IConverter;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Preconditions;

public class SetterExecutor implements IPropertyExecutor {

	private final Field field;

	private final IFieldManipulator manipulator;

	public SetterExecutor(Field field, IFieldManipulator manipulator) {
		this.field = field;
		this.manipulator = manipulator;
	}

	@Override
	public Object[] call(IConverter converter, Object target, Object... args) {
		Preconditions.checkArgument(args.length == 1, "Setter must have exactly one argument");
		Object arg = args[0];
		Object converted = converter.toJava(arg, field.getGenericType());
		manipulator.setField(target, field, converted);

		return ArrayUtils.EMPTY_OBJECT_ARRAY;
	}
}