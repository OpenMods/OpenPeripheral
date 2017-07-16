package openperipheral.adapter.types;

import openperipheral.api.adapter.IScriptType;

public class SingleType implements IScriptType {

	public static final IScriptType VOID = new SingleType("void");

	public static final IScriptType WILDCHAR = new SingleType("*");
	public static final IScriptType UNKNOWN = new SingleType("?");
	public static final IScriptType TAIL = new SingleType("...");

	public static final IScriptType NUMBER = new SingleType("number");
	public static final IScriptType STRING = new SingleType("string");
	public static final IScriptType TABLE = new SingleType("table");
	public static final IScriptType BOOLEAN = new SingleType("boolean");
	public static final IScriptType UUID = new SingleType("uuid");
	public static final IScriptType OBJECT = new SingleType("object");

	private final String type;

	public SingleType(String type) {
		this.type = type;
	}

	@Override
	public String describe() {
		return type;
	}

}
