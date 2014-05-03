package openperipheral.adapter.composed;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import openmods.Log;
import openperipheral.adapter.AdapterManager;
import openperipheral.adapter.IAdapterMethodsList;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.method.MethodDeclaration;
import openperipheral.api.LuaCallable;

import com.google.common.collect.Maps;

public abstract class ClassMethodsListBuilder<E extends IMethodExecutor> {
	private final AdapterManager<?, E> manager;

	private Map<String, E> methods = Maps.newHashMap();

	public static final String ARG_TARGET = "target";

	public ClassMethodsListBuilder(AdapterManager<?, E> manager) {
		this.manager = manager;
	}

	protected abstract E createDummyWrapper(Object lister, MethodDeclaration method);

	public void addMethodsFromObject(Object target) {
		addMethodsFromObject(methods, target);
	}

	private void addMethodsFromObject(Map<String, E> methods, Object target) {
		for (Method method : target.getClass().getMethods()) {
			LuaCallable callableMeta = method.getAnnotation(LuaCallable.class);
			if (callableMeta != null) {
				MethodDeclaration decl = new MethodDeclaration(method, callableMeta);
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
			for (String name : executor.getWrappedMethod().getNames()) {
				final E previous = methods.put(name, executor);
				if (previous != null) Log.fine("Previous defininition of Lua method '%s' overwritten by %s adapter", name, wrapper.describeType());
			}
		}
	}

	public Map<String, E> getMethodList() {
		return Collections.unmodifiableMap(methods);
	}

	public ClassMethodsList<E> create() {
		ClassMethodsList<E> result = new ClassMethodsList<E>(methods);
		methods = Maps.newHashMap();
		return result;
	}
}
