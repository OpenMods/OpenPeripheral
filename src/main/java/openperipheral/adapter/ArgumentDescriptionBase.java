package openperipheral.adapter;

import java.util.Set;

import openperipheral.adapter.IMethodDescription.IArgumentDescription;
import openperipheral.adapter.types.IType;

import com.google.common.collect.Sets;

public class ArgumentDescriptionBase implements IArgumentDescription {

	protected final String name;

	protected final IType type;

	protected String range;

	protected String description;

	public ArgumentDescriptionBase(String name, IType type, String range, String description) {
		this.name = name;
		this.type = type;
		this.range = range;
		this.description = description;
	}

	public ArgumentDescriptionBase(String name, IType type, String description) {
		this(name, type, "", description);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public IType type() {
		return type;
	}

	@Override
	public String range() {
		return range;
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
