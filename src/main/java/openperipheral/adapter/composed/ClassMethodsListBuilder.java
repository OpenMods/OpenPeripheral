package openperipheral.adapter.composed;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import openmods.Log;
import openperipheral.adapter.AdapterRegistry;
import openperipheral.adapter.IDescriptable;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.wrappers.AdapterWrapper;
import openperipheral.adapter.wrappers.TechnicalAdapterWrapper;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ClassMethodsListBuilder {
	private final AdapterRegistry manager;

	private final Map<String, IMethodExecutor> methods = Maps.newHashMap();

	private final Set<String> sources = Sets.newHashSet();

	public ClassMethodsListBuilder(AdapterRegistry manager) {
		this.manager = manager;
	}

	public void addExternalAdapters(Class<?> targetCls, Class<?> superClass) {
		for (AdapterWrapper wrapper : manager.getExternalAdapters(superClass))
			if (wrapper.canUse(targetCls)) addMethods(wrapper);
			else Log.warn("Adapter %s cannot be used for %s due to constraints", wrapper.describe());
	}

	public void addInlineAdapter(Class<?> cls) {
		AdapterWrapper wrapper = manager.getInlineAdapter(cls);
		addMethods(wrapper);
	}

	public void addMethods(AdapterWrapper wrapper) {
		for (IMethodExecutor executor : wrapper.getMethods()) {
			final IDescriptable descriptable = executor.description();
			sources.add(descriptable.source());
			for (String name : descriptable.getNames()) {
				final IMethodExecutor previous = methods.put(name, executor);
				if (previous != null) Log.trace("Previous defininition of Lua method '%s' overwritten by %s adapter", name, wrapper.describe());
			}
		}
	}

	public Map<String, IMethodExecutor> getMethodList() {
		return Collections.unmodifiableMap(methods);
	}

	public Set<String> getSources() {
		return Collections.unmodifiableSet(sources);
	}

	public boolean hasMethods() {
		return !methods.isEmpty();
	}

	public Map<String, IMethodExecutor> create() {
		return ImmutableMap.copyOf(methods);
	}

	public void addMethodsFromObject(Object obj, Class<?> targetCls, String source) {
		addMethods(new TechnicalAdapterWrapper(obj, targetCls, source));
	}
}
