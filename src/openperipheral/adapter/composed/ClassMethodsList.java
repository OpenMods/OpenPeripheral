package openperipheral.adapter.composed;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import openperipheral.adapter.IMethodExecutor;

import com.google.common.collect.ImmutableMap;

public class ClassMethodsList<E extends IMethodExecutor> {

	private final Map<Integer, E> methodsByIndex;
	public final String[] methodNames;
	public final boolean hasMethods;

	ClassMethodsList(Map<String, E> methodsByName) {
		ImmutableMap.Builder<Integer, E> methodsByIndex = ImmutableMap.builder();
		methodNames = new String[methodsByName.size()];
		int id = 0;

		boolean hasMethods = false;
		for (Map.Entry<String, E> e : methodsByName.entrySet()) {
			final E executor = e.getValue();

			methodNames[id] = e.getKey();
			methodsByIndex.put(id++, executor);
			hasMethods |= !executor.isSynthetic();
		}

		this.hasMethods = hasMethods;
		this.methodsByIndex = methodsByIndex.build();
	}

	public E getMethod(int index) {
		return methodsByIndex.get(index);
	}

	public Collection<E> getMethods() {
		return Collections.unmodifiableCollection(methodsByIndex.values());
	}
}
