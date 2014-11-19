package openperipheral.adapter;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class WrappedEntityBase<E extends IMethodExecutor> {

	private final String[] names;
	private final List<E> methods;

	public WrappedEntityBase(Map<String, E> list) {
		List<String> names = Lists.newArrayList();
		ImmutableList.Builder<E> methods = ImmutableList.builder();

		for (Map.Entry<String, E> e : list.entrySet()) {
			names.add(e.getKey());
			methods.add(e.getValue());
		}

		this.methods = methods.build();
		this.names = names.toArray(new String[0]);
	}

	public String[] getMethodNames() {
		return names;
	}

	public E getMethod(int index) {
		return methods.get(index);
	}

	public String getMethodName(int index) {
		return names[index];
	}
}
