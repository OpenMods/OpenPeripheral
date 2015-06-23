package openperipheral.adapter.method;

import openperipheral.adapter.types.TypeHelper;
import openperipheral.api.adapter.IScriptType;
import openperipheral.api.adapter.method.ArgType;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;

public class ArgumentBuilder {

	private boolean isVararg;
	private boolean isNullable;
	private boolean isOptional;

	public void setVararg(boolean isVararg) {
		this.isVararg = isVararg;
	}

	public void setNullable(boolean isNullable) {
		this.isNullable = isNullable;
	}

	public void setOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}

	public Argument build(String name, String description, ArgType luaType, TypeToken<?> javaType, int javaArgIndex) {
		final IScriptType wrappedType = TypeHelper.interpretArgType(luaType, javaType.getType());

		if (isVararg) {
			if (isNullable) return new NullableVarArgument(name, description, wrappedType, javaType, javaArgIndex);
			else return new VarArgument(name, description, wrappedType, javaType, javaArgIndex);
		}

		if (isOptional) {
			Preconditions.checkState(!isNullable, "Conflicting annotations on argument '%s'", name);
			return new OptionalArgument(name, description, wrappedType, javaType, javaArgIndex);
		}

		if (isNullable) return new NullableArgument(name, description, wrappedType, javaType, javaArgIndex);

		return new Argument(name, description, wrappedType, javaType, javaArgIndex);
	}
}
