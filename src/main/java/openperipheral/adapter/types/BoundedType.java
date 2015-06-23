package openperipheral.adapter.types;

import openperipheral.api.adapter.IScriptType;

public class BoundedType implements IScriptType {

	public final IScriptType type;

	public final IRange range;

	public BoundedType(IScriptType type, IRange range) {
		this.type = type;
		this.range = range;
	}

	@Override
	public String describe() {
		return type.describe() + range.describe();
	}

}
