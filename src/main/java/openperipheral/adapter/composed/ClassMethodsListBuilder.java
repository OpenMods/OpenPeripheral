package openperipheral.adapter.composed;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import openmods.Log;
import openperipheral.adapter.*;
import openperipheral.adapter.method.MethodDeclaration;
import openperipheral.api.LuaCallable;

import com.google.common.collect.Sets;

public abstract class ClassMethodsListBuilder<E extends IMethodExecutor> {
	private final AdapterManager<E> manager;

	private final MethodMap<E> methods = new MethodMap<E>();

	private final Set<String> sources = Sets.newHashSet();

	public static final String ARG_TARGET = "target";

	public ClassMethodsListBuilder(AdapterManager<E> manager) {
		this.manager = manager;
	}

	protected abstract E createDummyWrapper(Object lister, MethodDeclaration method);

	public void addMethodsFromObject(Object target, String source) {
		for (Method method : target.getClass().getMethods()) {
			LuaCallable callableMeta = method.getAnnotation(LuaCallable.class);
			if (callableMeta != null) {
				MethodDeclaration decl = new MethodDeclaration(method, callableMeta, source);
				sources.add(decl.source());
				for (String name : decl.getNames())
					methods.put(name, createDummyWrapper(target, decl));
			}
		}
	}

	public void addExternalAdapters(Class<?> targetCls, Class<?> superClass) {
		for (AdapterWrapper<E> wrapper : manager.getExternalAdapters(superClass))
			if (wrapper.canUse(targetCls)) addMethods(wrapper);
			else Log.warn("Adapter %s cannot be used for %s due to constraints", wrapper.describe());
	}

	public void addInlineAdapter(Class<?> cls) {
		AdapterWrapper<E> wrapper = manager.getInlineAdapter(cls);
		addMethods(wrapper);
	}

	public void addMethods(AdapterWrapper<E> wrapper) {
		for (E executor : wrapper.getMethods()) {
			final IDescriptable descriptable = executor.description();
			sources.add(descriptable.source());
			for (String name : descriptable.getNames()) {
				final E previous = methods.put(name, executor);
				if (previous != null) Log.trace("Previous defininition of Lua method '%s' overwritten by %s adapter", name, wrapper.describe());
			}
		}
	}

	public Map<String, E> getMethodList() {
		return Collections.unmodifiableMap(methods);
	}

	public Set<String> getSources() {
		return Collections.unmodifiableSet(sources);
	}

	public boolean hasMethods() {
		return !methods.isEmpty();
	}

	public MethodMap<E> create() {
		return methods;
	}
}
