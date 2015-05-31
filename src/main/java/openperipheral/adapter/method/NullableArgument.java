package openperipheral.adapter.method;

import java.util.Iterator;

import openperipheral.adapter.types.IType;
import openperipheral.api.converter.IConverter;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;

public class NullableArgument extends Argument {

	public NullableArgument(String name, String description, IType luaType, TypeToken<?> javaType, int javaArgIndex) {
		super(name, description, luaType, javaType, javaArgIndex);
	}

	@Override
	protected TypeToken<?> getArgType(TypeToken<?> javaArgClass) {
		Preconditions.checkArgument(!javaArgClass.isPrimitive(), "Nullable arguments can't be primitive");
		return super.getArgType(javaArgClass);
	}

	@Override
	public Object convert(IConverter converter, Iterator<Object> args) {
		Preconditions.checkArgument(args.hasNext(), "Not enough arguments, first missing: %s", name);
		Object arg = args.next();
		return convertSingleArg(converter, arg);
	}

	@Override
	public boolean nullable() {
		return true;
	}

}
