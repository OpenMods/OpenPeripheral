package openperipheral.adapter.method;

import java.util.Iterator;
import java.util.Map;

import openperipheral.api.ITypeConvertersRegistry;
import openperipheral.api.LuaArgType;

import com.google.common.base.Preconditions;

public class OptionalArgument extends Argument {

	public OptionalArgument(String name, String description, LuaArgType luaType, Class<?> javaType, int javaArgIndex) {
		super(name, description, luaType, javaType, javaArgIndex);
	}

	@Override
	protected Class<?> getArgType(Class<?> javaArgClass) {
		Preconditions.checkArgument(!javaArgClass.isPrimitive(), "Optional arguments can't be primitive");
		return super.getArgType(javaArgClass);
	}

	@Override
	public Object convert(ITypeConvertersRegistry converter, Iterator<Object> args) {
		if (!args.hasNext()) return null;

		Object arg = args.next();
		return convertSingleArg(converter, arg);
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

	@Override
	public String doc() {
		return super.doc() + "?";
	}
}
