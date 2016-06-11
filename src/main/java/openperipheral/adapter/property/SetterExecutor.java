package openperipheral.adapter.property;

import com.google.common.base.Preconditions;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import openperipheral.api.converter.IConverter;
import openperipheral.converter.TypeConverter;
import org.apache.commons.lang3.ArrayUtils;

public class SetterExecutor implements IPropertyExecutor {

	private final Field field;

	private final IFieldManipulator manipulator;

	private final SingleTypeInfo typeInfo;

	private final ISinglePropertyAccessHandler accessHandler;

	private final boolean nullable;

	public SetterExecutor(Field field, IFieldManipulator manipulator, SingleTypeInfo typeInfo, ISinglePropertyAccessHandler accessHandler, boolean nullable) {
		this.field = field;
		this.manipulator = manipulator;
		this.typeInfo = typeInfo;
		this.accessHandler = accessHandler;
		this.nullable = nullable;
	}

	@Override
	public Object[] call(IConverter converter, Object owner, Object... args) {
		Preconditions.checkArgument(args.length == 1, "Setter must have exactly one argument");
		final Object arg = args[0];

		final Object target = PropertyUtils.getContents(owner, field);
		final Type valueType = typeInfo.getValueType(target);
		final Object converted = TypeConverter.nullableToJava(converter, nullable, arg, valueType);
		accessHandler.onSet(owner, target, field, converted);
		manipulator.setField(owner, target, field, converted);

		return ArrayUtils.EMPTY_OBJECT_ARRAY;
	}
}