package openperipheral.adapter.method;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import java.util.Iterator;
import openperipheral.adapter.DefaultAttributeProperty;
import openperipheral.adapter.IAttributeProperty;
import openperipheral.api.adapter.method.ArgType;
import openperipheral.api.converter.IConverter;

public class OptionalArgument extends Argument {

	public OptionalArgument(String name, String description, ArgType luaType, TypeToken<?> javaType, int javaArgIndex) {
		super(name, description, luaType, javaType, javaArgIndex);
	}

	@Override
	protected TypeToken<?> getValueType(TypeToken<?> javaArgClass) {
		Preconditions.checkArgument(!javaArgClass.isPrimitive(), "Optional arguments can't be primitive");
		return super.getValueType(javaArgClass);
	}

	@Override
	public Object convert(IConverter converter, Iterator<Object> args) {
		if (!args.hasNext()) return null;

		Object arg = args.next();
		return arg != null? convertSingleArg(converter, arg) : null;
	}

	@Override
	public String toString() {
		return name + "?";
	}

	@Override
	public boolean is(IAttributeProperty property) {
		return property == DefaultAttributeProperty.OPTIONAL || super.is(property);
	}

}
