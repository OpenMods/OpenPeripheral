package openperipheral.adapter.property;

import java.util.List;
import java.util.Set;

import openperipheral.adapter.IMethodDescription;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

public abstract class PropertyDescriptionBase implements IMethodDescription {
	private final String name;
	protected final String description;
	private final String source;

	protected PropertyDescriptionBase(String name, String description, String source) {
		this.name = name;
		this.description = description;
		this.source = source;
	}

	@Override
	public List<String> getNames() {
		return ImmutableList.of(name);
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
	public Set<String> attributes() {
		return Sets.newHashSet();
	}
}