package openperipheral.adapter.method;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import java.util.Iterator;
import openperipheral.adapter.ArgumentDescriptionBase;
import openperipheral.api.adapter.IScriptType;
import openperipheral.api.converter.IConverter;

public class Argument extends ArgumentDescriptionBase {

	public final TypeToken<?> javaType;
	final int javaArgIndex;

	public Argument(String name, String description, IScriptType type, TypeToken<?> javaType, int javaArgIndex) {
		super(name, type, description);
		this.javaArgIndex = javaArgIndex;
		this.javaType = getArgType(javaType);
	}

	protected TypeToken<?> getArgType(TypeToken<?> javaArgClass) {
		return javaArgClass;
	}

	public Object convert(IConverter converter, Iterator<Object> args) {
		Preconditions.checkArgument(args.hasNext(), "Not enough arguments, first missing: %s", name);
		Object arg = args.next();
		Preconditions.checkArgument(arg != null, "Argument %s cannot be null", name);
		return convertSingleArg(converter, arg);
	}

	protected final Object convertSingleArg(IConverter converter, Object o) {
		try {
			return converter.toJava(o, javaType.getType());
		} catch (Exception e) {
			throw new IllegalArgumentException(String.format("Failed to convert arg '%s', cause: '%s'", name, e.getMessage()));
		}
	}

	@Override
	public String toString() {
		return name + "(" + javaArgIndex + "):" + javaType;
	}
}