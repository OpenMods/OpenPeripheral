package openperipheral.adapter.types;

import openperipheral.api.adapter.method.ArgType;

public class SingleArgType implements IType {

	public final ArgType type;

	public SingleArgType(ArgType type) {
		this.type = type;
	}

	@Override
	public String describe() {
		return type.getName();
	}

}
