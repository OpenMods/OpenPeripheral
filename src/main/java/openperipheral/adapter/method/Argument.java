package openperipheral.adapter.method;

import java.util.Iterator;
import java.util.Map;

import openperipheral.adapter.IDescriptable;
import openperipheral.api.adapter.method.ArgType;
import openperipheral.api.converter.IConverter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

public class Argument {
	public final String name;
	public final String description;
	public final ArgType luaType;
	public final TypeToken<?> javaType;
	final int javaArgIndex;

	public Argument(String name, String description, ArgType luaType, TypeToken<?> javaType, int javaArgIndex) {
		this.name = name;
		this.description = description;
		this.luaType = luaType;
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
		if (o == null) return null;
		Object converted = converter.toJava(o, javaType.getType());
		Preconditions.checkArgument(converted != null, "Failed to convert arg '%s' value '%s' to '%s'", name, o, javaType);
		return converted;
	}

	public Map<String, Object> describe() {
		Map<String, Object> result = Maps.newHashMap();
		result.put(IDescriptable.TYPE, luaType.toString());
		result.put(IDescriptable.NAME, name);
		result.put(IDescriptable.DESCRIPTION, description);
		return result;
	}

	@Override
	public String toString() {
		return name;
	}

	public String doc() {
		return luaType.getName();
	}
}