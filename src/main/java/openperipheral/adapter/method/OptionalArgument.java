package openperipheral.adapter.method;

import java.util.Iterator;
import java.util.Map;

import openperipheral.api.LuaType;

import com.google.common.base.Preconditions;

public class OptionalArgument extends Argument {

	public OptionalArgument(String name, String description, LuaType luaType, Class<?> javaType, int javaArgIndex) {
		super(name, description, luaType, javaType, javaArgIndex);
	}

	@Override
	protected Class<?> getArgType(Class<?> javaArgClass) {
		Preconditions.checkArgument(!javaArgClass.isPrimitive(), "Optional arguments can't be primitive");
		return super.getArgType(javaArgClass);
	}

	@Override
	public Object convert(Iterator<Object> args) {
		if (!args.hasNext()) return null;

		Object arg = args.next();
		return convertSingleArg(arg);
	}

	@Override
	public Map<String, Object> describe() {
		Map<String, Object> result = super.describe();
		result.put("optional", true);
		return result;
	}

	@Override
	public String toString() {
		return name + "?";
	}
}
