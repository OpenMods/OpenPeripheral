package openperipheral.adapter.method;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;

import openperipheral.api.adapter.IScriptType;
import openperipheral.api.converter.IConverter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

public class VarArgument extends Argument {

	public VarArgument(String name, String description, IScriptType luaType, TypeToken<?> javaType, int javaArgIndex) {
		super(name, description, luaType, javaType, javaArgIndex);
	}

	@Override
	protected TypeToken<?> getArgType(TypeToken<?> javaArgClass) {
		// something went terribly wrong
		Preconditions.checkArgument(javaArgClass.isArray(), "Vararg type must be array");
		return javaArgClass.getComponentType();
	}

	protected void checkArgument(Object value) {
		Preconditions.checkArgument(value != null, "Vararg parameter '%s' has null value, but is not marked as nullable", name);
	}

	@Override
	public Object convert(IConverter converter, Iterator<Object> args) {
		List<Object> allArgs = Lists.newArrayList(args);

		Object vararg = Array.newInstance(javaType.getRawType(), allArgs.size());

		for (int i = 0; i < allArgs.size(); i++) {
			Object value = allArgs.get(i);
			checkArgument(value);
			Object converted = convertSingleArg(converter, value);
			Array.set(vararg, i, converted);
		}

		return vararg;
	}

	@Override
	public String toString() {
		return name + "...";
	}

	@Override
	public boolean variadic() {
		return true;
	}

}
