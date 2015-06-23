package openperipheral.adapter.types;

import openperipheral.api.adapter.IScriptType;
import openperipheral.api.adapter.method.ReturnType;

import com.google.common.base.Preconditions;

public class SingleReturnType implements IScriptType {

	public static final SingleReturnType TABLE = new SingleReturnType(ReturnType.TABLE);
	public static final SingleReturnType NUMBER = new SingleReturnType(ReturnType.NUMBER);
	public static final SingleReturnType VOID = new SingleReturnType(ReturnType.VOID);
	public static final SingleReturnType BOOLEAN = new SingleReturnType(ReturnType.BOOLEAN);
	public static final SingleReturnType STRING = new SingleReturnType(ReturnType.STRING);
	public static final SingleReturnType OBJECT = new SingleReturnType(ReturnType.OBJECT);

	public final ReturnType type;

	private SingleReturnType(ReturnType type) {
		this.type = type;
	}

	@Override
	public String describe() {
		return type.getName();
	}

	public static SingleReturnType valueOf(ReturnType type) {
		Preconditions.checkNotNull(type);
		switch (type) {
			case BOOLEAN:
				return BOOLEAN;
			case NUMBER:
				return NUMBER;
			case OBJECT:
				return OBJECT;
			case STRING:
				return STRING;
			case TABLE:
				return TABLE;
			case VOID:
				return VOID;
			default:
				return new SingleReturnType(type);
		}
	}
}
