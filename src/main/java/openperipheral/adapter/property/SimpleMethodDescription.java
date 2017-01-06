package openperipheral.adapter.property;

import com.google.common.collect.ImmutableList;
import java.util.List;
import openperipheral.adapter.IMethodDescription;
import openperipheral.api.adapter.IScriptType;

public class SimpleMethodDescription implements IMethodDescription {
	private final List<String> names;
	private final String description;
	private final String source;
	private final List<IArgumentDescription> arguments;
	private final IScriptType returnType;

	public SimpleMethodDescription(String name, String description, String source, List<IArgumentDescription> arguments, IScriptType returnType) {
		this.names = ImmutableList.of(name);
		this.description = description;
		this.source = source;
		this.arguments = ImmutableList.copyOf(arguments);
		this.returnType = returnType;
	}

	@Override
	public List<String> getNames() {
		return names;
	}

	@Override
	public String source() {
		return source;
	}

	@Override
	public String description() {
		return description;
	}

	@Override
	public List<IArgumentDescription> arguments() {
		return arguments;
	}

	@Override
	public IScriptType returnTypes() {
		return returnType;
	}
}