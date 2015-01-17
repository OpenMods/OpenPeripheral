package openperipheral.adapter;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class WrappedEntityBase {

	protected final String[] names;
	protected final List<IMethodExecutor> methods;

	public WrappedEntityBase(Map<String, IMethodExecutor> list) {
		List<String> names = Lists.newArrayList();
		ImmutableList.Builder<IMethodExecutor> methods = ImmutableList.builder();

		for (Map.Entry<String, IMethodExecutor> e : list.entrySet()) {
			names.add(e.getKey());
			methods.add(e.getValue());
		}

		this.methods = methods.build();
		this.names = names.toArray(new String[0]);
	}

	public String[] getMethodNames() {
		return names;
	}

	public IMethodExecutor getMethod(int index) {
		return methods.get(index);
	}

	public String getMethodName(int index) {
		return names[index];
	}
}
