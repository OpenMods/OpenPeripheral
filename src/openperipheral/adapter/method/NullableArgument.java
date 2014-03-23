package openperipheral.adapter.method;

import java.util.Iterator;
import java.util.Map;

import openperipheral.api.LuaType;

import com.google.common.base.Preconditions;

public class NullableArgument extends Argument {

	public NullableArgument(String name, String description, LuaType luaType, Class<?> javaType, int javaArgIndex) {
		super(name, description, luaType, javaType, javaArgIndex);
	}

	@Override
	protected Class<?> getArgType(Class<?> javaArgClass) {
		Preconditions.checkArgument(!javaArgClass.isPrimitive(), "Nullable arguments can't be primitive");
		return super.getArgType(javaArgClass);
	}

	@Override
	public Object convert(Iterator<Object> args) {
		Preconditions.checkState(args.hasNext(), "Not enough arguments, first missing: %s", name);
		Object arg = args.next();
		return convertSingleArg(arg);
	}

	@Override
	public Map<String, Object> describe() {
		Map<String, Object> result = super.describe();
		result.put("nullable", true);
		return result;
	}

}
