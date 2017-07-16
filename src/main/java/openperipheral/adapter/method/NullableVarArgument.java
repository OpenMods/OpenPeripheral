package openperipheral.adapter.method;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import openperipheral.adapter.DefaultAttributeProperty;
import openperipheral.adapter.IAttributeProperty;

public class NullableVarArgument extends VarArgument {

	public NullableVarArgument(String name, String description, TypeToken<?> javaType, int javaArgIndex) {
		super(name, description, javaType, javaArgIndex);
	}

	@Override
	protected TypeToken<?> getValueType(TypeToken<?> javaArgClass) {
		TypeToken<?> elementType = super.getValueType(javaArgClass);
		Preconditions.checkArgument(!elementType.isPrimitive(), "Nullable arguments can't be primitive");
		return elementType;
	}

	@Override
	protected void checkArgument(Object value) {}

	@Override
	public boolean is(IAttributeProperty property) {
		return property == DefaultAttributeProperty.NULLABLE || super.is(property);
	}

}
