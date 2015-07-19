package openperipheral.adapter.types;

import java.util.*;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class EnumeratedRange<T> implements IRange {

	public final Set<T> values;

	public EnumeratedRange(Collection<T> values) {
		this.values = ImmutableSet.copyOf(values);
	}

	@Override
	public String describe() {
		List<String> returns = Lists.newArrayList();
		for (T r : values)
			returns.add(r.toString());

		return "{" + Joiner.on(',').join(returns) + "}";
	}

	public static <T> EnumeratedRange<T> create(Collection<T> values) {
		return new EnumeratedRange<T>(values);
	}

	public static <T> EnumeratedRange<T> create(T... values) {
		return new EnumeratedRange<T>(Arrays.asList(values));
	}
}
