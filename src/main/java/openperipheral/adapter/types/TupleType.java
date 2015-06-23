package openperipheral.adapter.types;

import java.util.Collection;
import java.util.List;

import openperipheral.api.adapter.IScriptType;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class TupleType implements IScriptType {

	public final List<IScriptType> types;

	public TupleType(IScriptType... types) {
		this.types = ImmutableList.copyOf(types);
	}

	public TupleType(Collection<IScriptType> types) {
		this.types = ImmutableList.copyOf(types);
	}

	@Override
	public String describe() {
		List<String> returns = Lists.newArrayList();
		for (IScriptType r : types)
			returns.add(r.describe());

		return "(" + Joiner.on(',').join(returns) + ")";
	}

}
