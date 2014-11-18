package openperipheral.adapter.composed;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.IMethodsHolder;

import com.google.common.collect.ImmutableMap;

public class ClassMethodsList<E extends IMethodExecutor> implements IMethodsHolder<E> {

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
			hasMethods |= !executor.isGenerated();
		}

		this.hasMethods = hasMethods;
		this.methodsByIndex = methodsByIndex.build();
	}

	public E getMethod(int index) {
		return methodsByIndex.get(index);
	}

	@Override
	public Collection<E> listMethods() {
		return Collections.unmodifiableCollection(methodsByIndex.values());
	}
}
