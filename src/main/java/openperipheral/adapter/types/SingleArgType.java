package openperipheral.adapter.types;

import openperipheral.api.adapter.method.ArgType;

import com.google.common.base.Preconditions;

public class SingleArgType implements IType {

	public static final SingleArgType TABLE = new SingleArgType(ArgType.TABLE);
	public static final SingleArgType NUMBER = new SingleArgType(ArgType.NUMBER);
	public static final SingleArgType VOID = new SingleArgType(ArgType.VOID);
	public static final SingleArgType BOOLEAN = new SingleArgType(ArgType.BOOLEAN);
	public static final SingleArgType STRING = new SingleArgType(ArgType.STRING);
	public static final SingleArgType OBJECT = new SingleArgType(ArgType.OBJECT);

	public final ArgType type;

	private SingleArgType(ArgType type) {
		this.type = type;
	}

	@Override
	public String describe() {
		return type.getName();
	}

	public static SingleArgType valueOf(ArgType type) {
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
				return new SingleArgType(type);
		}
	}

}
