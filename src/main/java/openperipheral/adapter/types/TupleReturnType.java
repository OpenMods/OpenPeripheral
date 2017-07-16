package openperipheral.adapter.types;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import openperipheral.api.adapter.IScriptType;

public class TupleReturnType implements IScriptType {
	public final List<IScriptType> types;

	public TupleReturnType(IScriptType... types) {
		this.types = ImmutableList.copyOf(types);
	}

	public TupleReturnType(Collection<IScriptType> types) {
		this.types = ImmutableList.copyOf(types);
	}

	@Override
	public String describe() {
		List<String> returns = Lists.newArrayList();
		for (IScriptType r : types)
			returns.add(r.describe());

		return Joiner.on(',').join(returns);
	}
}
