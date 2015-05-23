package openperipheral.adapter.property;

import java.util.List;

import openperipheral.adapter.ArgumentDescriptionBase;
import openperipheral.api.adapter.method.ArgType;
import openperipheral.api.adapter.method.ReturnType;

import com.google.common.collect.ImmutableList;

public class SetterDescription extends PropertyDescriptionBase {

	public static final List<ReturnType> NO_RETURNS = ImmutableList.of();

	private final List<IArgumentDescription> arguments;

	protected SetterDescription(String capitalizedName, String description, ArgType type, String source) {
		super("set" + capitalizedName, description, source);

		final IArgumentDescription argument = new ArgumentDescriptionBase("value", type, description);
		this.arguments = ImmutableList.of(argument);
	}

	@Override
	public List<IArgumentDescription> arguments() {
		return arguments;
	}

	@Override
	public List<ReturnType> returnTypes() {
		return NO_RETURNS;
	}

}