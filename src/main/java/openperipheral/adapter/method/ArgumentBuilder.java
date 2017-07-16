package openperipheral.adapter.method;

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

	public Argument build(String name, String description, TypeToken<?> javaType, int javaArgIndex) {
		if (isVararg) {
			if (isNullable) return new NullableVarArgument(name, description, javaType, javaArgIndex);
			else return new VarArgument(name, description, javaType, javaArgIndex);
		}

		if (isOptional) {
			Preconditions.checkState(!isNullable, "Conflicting annotations on argument '%s: optional cannot be nullable'", name);
			return new OptionalArgument(name, description, javaType, javaArgIndex);
		}

		if (isNullable) return new NullableArgument(name, description, javaType, javaArgIndex);

		return new Argument(name, description, javaType, javaArgIndex);
	}
}
