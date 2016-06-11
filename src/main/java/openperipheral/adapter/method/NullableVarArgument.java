package openperipheral.adapter.method;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import openperipheral.api.adapter.IScriptType;

public class NullableVarArgument extends VarArgument {

	public NullableVarArgument(String name, String description, IScriptType luaType, TypeToken<?> javaType, int javaArgIndex) {
		super(name, description, luaType, javaType, javaArgIndex);
	}

	@Override
	protected TypeToken<?> getArgType(TypeToken<?> javaArgClass) {
		TypeToken<?> elementType = super.getArgType(javaArgClass);
		Preconditions.checkArgument(!elementType.isPrimitive(), "Nullable arguments can't be primitive");
		return elementType;
	}

	@Override
	protected void checkArgument(Object value) {}

	@Override
	public boolean nullable() {
		return true;
	}

}
