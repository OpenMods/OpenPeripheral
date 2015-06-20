package openperipheral.adapter.types;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class TupleType implements IType {

	public final List<IType> types;

	public TupleType(IType... types) {
		this.types = ImmutableList.copyOf(types);
	}

	public TupleType(Collection<IType> types) {
		this.types = ImmutableList.copyOf(types);
	}

	@Override
	public String describe() {
		List<String> returns = Lists.newArrayList();
		for (IType r : types)
			returns.add(r.describe());

		return "(" + Joiner.on(',').join(returns) + ")";
	}

}
