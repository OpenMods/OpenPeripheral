package openperipheral.adapter.method;

import java.util.Map;

import openperipheral.api.adapter.method.LuaArgType;

import com.google.common.base.Preconditions;

public class NullableVarArgument extends VarArgument {

	public NullableVarArgument(String name, String description, LuaArgType luaType, Class<?> javaType, int javaArgIndex) {
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
}
