package openperipheral.adapter.types;

import openperipheral.api.adapter.IScriptType;

public class VarReturnType implements IScriptType {
	public final IScriptType type;

	public VarReturnType(IScriptType type) {
		this.type = type;
	}

	@Override
	public String describe() {
		return type.describe() + "*";
	}
}
