package openperipheral.adapter;

import com.google.common.collect.Sets;
import java.util.Set;
import openperipheral.adapter.IMethodDescription.IArgumentDescription;
import openperipheral.api.adapter.IScriptType;

public class ArgumentDescriptionBase implements IArgumentDescription {

	protected final String name;

	protected final IScriptType type;

	protected String description;

	public ArgumentDescriptionBase(String name, IScriptType type, String description) {
		this.name = name;
		this.type = type;
		this.description = description;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public IScriptType type() {
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
