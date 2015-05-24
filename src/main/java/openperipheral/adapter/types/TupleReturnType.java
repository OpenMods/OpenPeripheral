package openperipheral.adapter.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class TupleReturnType implements IReturnType {
	private final String description;

	public TupleReturnType(IReturnType... returnTypes) {
		this(Arrays.asList(returnTypes));
	}

	public TupleReturnType(Collection<IReturnType> returnTypes) {

		List<String> returns = Lists.newArrayList();
		for (IReturnType r : returnTypes)
			returns.add(r.describe());
		this.description = "(" + Joiner.on(',').join(returns) + ")";
	}

	@Override
	public String describe() {
		return description;
	}
}
