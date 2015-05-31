package openperipheral.adapter.types;

import openperipheral.api.adapter.method.ReturnType;

public class SingleReturnType implements IType {

	private final ReturnType type;

	public SingleReturnType(ReturnType type) {
		this.type = type;
	}

	@Override
	public String describe() {
		return type.getName();
	}
}
