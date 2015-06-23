package openperipheral.adapter.types;

import openperipheral.api.adapter.IScriptType;

public class SetType implements IScriptType {

	public final IScriptType componentType;

	public SetType(IScriptType componentType) {
		this.componentType = componentType;
	}

	@Override
	public String describe() {
		return "{" + componentType.describe() + "}";
	}

}
