package openperipheral.adapter.types;

import openperipheral.api.adapter.IScriptType;

public class MapType implements IScriptType {

	public final IScriptType keyType;

	public final IScriptType valueType;

	public MapType(IScriptType keyType, IScriptType valueType) {
		this.keyType = keyType;
		this.valueType = valueType;
	}

	@Override
	public String describe() {
		return "{" + keyType.describe() + "->" + valueType.describe() + "}";
	}

}
