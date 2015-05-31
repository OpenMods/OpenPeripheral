package openperipheral.adapter.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class TupleType implements IType {
	private final String description;

	public TupleType(IType... returnTypes) {
		this(Arrays.asList(returnTypes));
	}

	public TupleType(Collection<IType> returnTypes) {

		List<String> returns = Lists.newArrayList();
		for (IType r : returnTypes)
			returns.add(r.describe());
		this.description = "(" + Joiner.on(',').join(returns) + ")";
	}

	@Override
	public String describe() {
		return description;
	}
}
