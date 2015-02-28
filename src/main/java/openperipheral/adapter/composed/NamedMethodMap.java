package openperipheral.adapter.composed;

import java.util.Map;

import openperipheral.adapter.IMethodExecutor;

import com.google.common.collect.ImmutableMap;

public class NamedMethodMap implements IMethodMap {

	private final Map<String, IMethodExecutor> methods;

	private final String[] methodNames;

	public NamedMethodMap(Map<String, IMethodExecutor> methods) {
		this.methods = ImmutableMap.copyOf(methods);
		this.methodNames = methods.keySet().toArray(new String[methods.size()]);
	}

	public String[] getMethodNames() {
		return methodNames;
	}

	public IMethodExecutor getMethod(String name) {
		return methods.get(name);
	}

	@Override
	public boolean isEmpty() {
		return methods.isEmpty();
	}

	@Override
	public int size() {
		return methods.size();
	}

	@Override
	public void visitMethods(IMethodVisitor visitor) {
		for (Map.Entry<String, IMethodExecutor> e : methods.entrySet())
			visitor.visit(e.getKey(), e.getValue());
	}
}
