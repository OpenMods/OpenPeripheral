package openperipheral.adapter.types;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import openperipheral.api.adapter.IScriptType;

public class NamedTupleType implements IScriptType {

	public static class TupleField {}

	public static TupleField TAIL = new TupleField() {
		@Override
		public String toString() {
			return "...";
		}
	};

	public static class NamedTupleField extends TupleField {
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

	public final List<TupleField> fields;

	public NamedTupleType(TupleField... fields) {
		this.fields = ImmutableList.copyOf(fields);
	}

	public NamedTupleType(Collection<TupleField> fields) {
		this.fields = ImmutableList.copyOf(fields);
	}

	@Override
	public String describe() {
		return "{" + Joiner.on(",").join(fields) + "}";

	}

}
