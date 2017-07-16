package openperipheral.adapter.method;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import java.util.Iterator;
import openperipheral.adapter.DefaultAttributeProperty;
import openperipheral.adapter.IAttributeProperty;
import openperipheral.api.converter.IConverter;

public class NullableArgument extends Argument {

	public NullableArgument(String name, String description, TypeToken<?> javaType, int javaArgIndex) {
		super(name, description, javaType, javaArgIndex);
	}

	@Override
	protected TypeToken<?> getValueType(TypeToken<?> javaArgClass) {
		Preconditions.checkArgument(!javaArgClass.isPrimitive(), "Nullable arguments can't be primitive");
		return super.getValueType(javaArgClass);
	}

	@Override
	public Object convert(IConverter converter, Iterator<Object> args) {
		Preconditions.checkArgument(args.hasNext(), "Not enough arguments, first missing: %s", name);
		Object arg = args.next();
		return convertSingleArg(converter, arg);
	}

	@Override
	public boolean is(IAttributeProperty property) {
		return property == DefaultAttributeProperty.NULLABLE || super.is(property);
	}

}
