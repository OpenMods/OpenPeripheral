package openperipheral.adapter.method;

import java.util.Iterator;

import openperipheral.api.adapter.IScriptType;
import openperipheral.api.converter.IConverter;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;

public class OptionalArgument extends Argument {

	public OptionalArgument(String name, String description, IScriptType luaType, TypeToken<?> javaType, int javaArgIndex) {
		super(name, description, luaType, javaType, javaArgIndex);
	}

	@Override
	protected TypeToken<?> getArgType(TypeToken<?> javaArgClass) {
		Preconditions.checkArgument(!javaArgClass.isPrimitive(), "Optional arguments can't be primitive");
		return super.getArgType(javaArgClass);
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
	public boolean optional() {
		return true;
	}

}
