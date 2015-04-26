package openperipheral.adapter;

import java.util.Set;

import openperipheral.adapter.IMethodDescription.IArgumentDescription;
import openperipheral.api.adapter.method.ArgType;

import com.google.common.collect.Sets;

public class ArgumentDescriptionBase implements IArgumentDescription {

	protected final String name;

	protected final ArgType type;

	protected final String description;

	public ArgumentDescriptionBase(String name, ArgType type, String description) {
		this.name = name;
		this.type = type;
		this.description = description;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public ArgType type() {
		return type;
	}

	@Override
	public String description() {
		return description;
	}

	@Override
	public boolean nullable() {
		return false;
	}

	@Override
	public boolean optional() {
		return false;
	}

	@Override
	public boolean variadic() {
		return false;
	}

	@Override
	public Set<String> attributes() {
		return Sets.newHashSet();
	}

}
