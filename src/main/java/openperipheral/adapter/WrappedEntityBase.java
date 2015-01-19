package openperipheral.adapter;

import java.util.Map;

public class WrappedEntityBase {

	protected final String[] names;
	protected final IMethodExecutor[] methods;

	public WrappedEntityBase(Map<String, IMethodExecutor> methods) {
		final int methodCount = methods.size();
		this.names = new String[methodCount];
		this.methods = new IMethodExecutor[methodCount];

		int i = 0;
		for (Map.Entry<String, IMethodExecutor> e : methods.entrySet()) {
			this.names[i] = e.getKey();
			this.methods[i] = e.getValue();
			i++;
		}
	}

	public IMethodExecutor[] getMethods() {
		return methods;
	}

	public String[] getMethodNames() {
		return names;
	}

	public IMethodExecutor getMethod(int index) {
		return methods[index];
	}

	public String getMethodName(int index) {
		return names[index];
	}
}
