package openperipheral.adapter.types;

import openperipheral.api.adapter.IScriptType;

public class SingleType implements IScriptType {

	public static IScriptType VOID = new SingleType("()");
	public static IScriptType WILDCHAR = new SingleType("*");
	public static IScriptType UNKNOWN = new SingleType("?");

	private final String type;

	public SingleType(String type) {
		this.type = type;
	}

	@Override
	public String describe() {
		return type;
	}

}
