package openperipheral.adapter.method;

import openperipheral.api.adapter.method.ArgType;

import com.google.common.base.Preconditions;

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

	public Argument build(String name, String description, ArgType luaType, Class<?> javaType, int javaArgIndex) {
		if (luaType == ArgType.AUTO) luaType = LuaTypeQualifier.qualifyArgType(javaType);

		if (isVararg) {
			if (isNullable) return new NullableVarArgument(name, description, luaType, javaType, javaArgIndex);
			else return new VarArgument(name, description, luaType, javaType, javaArgIndex);
		}

		if (isOptional) {
			Preconditions.checkState(!isNullable, "Conflicting annotations on argument '%s'", name);
			return new OptionalArgument(name, description, luaType, javaType, javaArgIndex);
		}

		if (isNullable) return new NullableArgument(name, description, luaType, javaType, javaArgIndex);

		return new Argument(name, description, luaType, javaType, javaArgIndex);
	}
}
