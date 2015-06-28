package openperipheral.adapter.types;

import java.util.Collection;
import java.util.List;

import openperipheral.api.adapter.IScriptType;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

public class NamedTupleType implements IScriptType {

	public static class NamedTupleField {
		public final String name;
		public final IScriptType type;
		public final boolean isOptional;

		public NamedTupleField(String name, IScriptType type, boolean isOptional) {
			this.name = name;
			this.type = type;
			this.isOptional = isOptional;
		}

		@Override
		public String toString() {
			return name + ":" + type.describe() + (isOptional? "?" : "");
		}
	}

	public final List<NamedTupleField> fields;

	public NamedTupleType(Collection<NamedTupleField> fields) {
		this.fields = ImmutableList.copyOf(fields);
	}

	@Override
	public String describe() {
		return "{" + Joiner.on(",").join(fields) + "}";

	}

}
