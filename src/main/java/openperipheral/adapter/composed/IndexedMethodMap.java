package openperipheral.adapter.composed;

import java.util.Map;

import openperipheral.adapter.IMethodExecutor;

public class IndexedMethodMap implements IMethodMap {

	protected final String[] names;
	protected final IMethodExecutor[] methods;

	public IndexedMethodMap(Map<String, IMethodExecutor> methods) {
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

	@Override
	public boolean isEmpty() {
		return names.length == 0;
	}

	@Override
	public void visitMethods(IMethodVisitor visitor) {
		for (int i = 0; i < names.length; i++)
			visitor.visit(names[i], methods[i]);
	}
}
