package openperipheral.adapter.composed;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import openmods.Log;
import openperipheral.adapter.*;
import openperipheral.adapter.method.MethodDeclaration;
import openperipheral.api.LuaCallable;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public abstract class ClassMethodsListBuilder<E extends IMethodExecutor> {
	private final AdapterManager<?, E> manager;

	private final Map<String, E> methods = Maps.newHashMap();

	private final Set<String> sources = Sets.newHashSet();

	public static final String ARG_TARGET = "target";

	public ClassMethodsListBuilder(AdapterManager<?, E> manager) {
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

	public void addExternalAdapters(Class<?> cls) {
		for (IAdapterMethodsList<E> wrapper : manager.getExternalAdapters(cls))
			addMethods(wrapper);
	}

	public void addInlineAdapter(Class<?> cls) {
		IAdapterMethodsList<E> wrapper = manager.getInlineAdapter(cls);
		addMethods(wrapper);
	}

	public void addMethods(IAdapterMethodsList<E> wrapper) {
		for (E executor : wrapper.listMethods()) {
			final IDescriptable descriptable = executor.getWrappedMethod();
			sources.add(descriptable.source());
			for (String name : descriptable.getNames()) {
				final E previous = methods.put(name, executor);
				if (previous != null) Log.trace("Previous defininition of Lua method '%s' overwritten by %s adapter", name, wrapper.describeType());
			}
		}
	}

	public Map<String, E> getMethodList() {
		return Collections.unmodifiableMap(methods);
	}

	public Set<String> getSources() {
		return Collections.unmodifiableSet(sources);
	}

	public ClassMethodsList<E> create() {
		return new ClassMethodsList<E>(methods);
	}
}
