package openperipheral.adapter.method;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import java.util.Iterator;
import openperipheral.api.adapter.IScriptType;
import openperipheral.api.converter.IConverter;

public class NullableArgument extends Argument {

	public NullableArgument(String name, String description, IScriptType luaType, TypeToken<?> javaType, int javaArgIndex) {
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
