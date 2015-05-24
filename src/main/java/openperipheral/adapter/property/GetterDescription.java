package openperipheral.adapter.property;

import java.util.List;

import openperipheral.adapter.types.IReturnType;
import openperipheral.adapter.types.SingleReturnType;
import openperipheral.api.adapter.method.ArgType;
import openperipheral.api.adapter.method.ReturnType;

import com.google.common.collect.ImmutableList;

public class GetterDescription extends PropertyDescriptionBase {

	public static final List<IArgumentDescription> EMPTY_ARGS = ImmutableList.of();

	private final IReturnType returnType;

	static ReturnType convert(ArgType type) {
		switch (type) {
			case BOOLEAN:
				return ReturnType.BOOLEAN;
			case NUMBER:
				return ReturnType.NUMBER;
			case STRING:
				return ReturnType.STRING;
			case TABLE:
				return ReturnType.TABLE;
			default:
				return ReturnType.OBJECT;
		}
	}

	protected GetterDescription(String capitalizedName, String description, ArgType type, String source) {
		super("get" + capitalizedName, description, source);
		final ReturnType returnType = convert(type);
		this.returnType = new SingleReturnType(returnType);
	}

	@Override
	public List<IArgumentDescription> arguments() {
		return EMPTY_ARGS;
	}

	@Override
	public IReturnType returnTypes() {
		return returnType;
	}
}