package openperipheral.adapter.method;

import java.util.Map;

import openperipheral.api.LuaType;

import com.google.common.base.Preconditions;

public class NullableVarArgument extends VarArgument {

	public NullableVarArgument(String name, String description, LuaType luaType, Class<?> javaType, int javaArgIndex) {
		super(name, description, luaType, javaType, javaArgIndex);
	}

	@Override
	protected Class<?> getArgType(Class<?> javaArgClass) {
		Class<?> elementType = super.getArgType(javaArgClass);
		Preconditions.checkArgument(!elementType.isPrimitive(), "Nullable arguments can't be primitive");
		return elementType;
	}

	@Override
	protected void checkArgument(Object value) {}

	@Override
	public Map<String, Object> describe() {
		Map<String, Object> result = super.describe();
		result.put("nullable", true);
		return result;
	}

	@Override
	public String toString() {
		return name + "?...";
	}

}
