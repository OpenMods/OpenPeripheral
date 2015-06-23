package openperipheral.adapter.types;

import openperipheral.api.adapter.IScriptType;

public class ListType implements IScriptType {

	public final IScriptType componentType;

	public ListType(IScriptType componentType) {
		this.componentType = componentType;
	}

	@Override
	public String describe() {
		return "[" + componentType.describe() + "]";
	}

}
