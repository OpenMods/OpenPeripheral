package openperipheral.adapter;

import java.lang.reflect.Method;
import java.util.List;

import openmods.Log;
import openperipheral.adapter.method.MethodDeclaration;
import openperipheral.api.LuaCallable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public abstract class AdapterWrapper<E extends IMethodExecutor> {

	public static class MethodWrapException extends RuntimeException {
		private static final long serialVersionUID = -5116134133615320058L;

		public MethodWrapException(Method method, Throwable cause) {
			super(String.format("Failed to wrap method '%s'", method), cause);
		}
	}

	protected final List<E> methods;
	protected final Class<?> targetClass;
	protected final Class<?> adapterClass;
	protected final String source;

	protected AdapterWrapper(Class<?> adapterClass, Class<?> targetClass, String source) {
		this.adapterClass = adapterClass;
		this.targetClass = targetClass;
		this.source = source;
		this.methods = ImmutableList.copyOf(buildMethodList());
	}

	public String source() {
		return source;
	}

	public List<E> getMethods() {
		return methods;
	}

	public Class<?> getAdapterClass() {
		return adapterClass;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public abstract boolean canUse(Class<?> cls);

	public abstract String describe();

	protected abstract List<E> buildMethodList();

	protected abstract void configureJavaArguments(MethodDeclaration decl);

	protected interface MethodExecutorFactory<E extends IMethodExecutor> {
		public E createExecutor(Method method, MethodDeclaration decl);
	}

	protected List<E> buildMethodList(MethodExecutorFactory<E> factory) {
		List<E> result = Lists.newArrayList();

		Method[] clsMethods;
		try {
			clsMethods = adapterClass.getDeclaredMethods();
		} catch (Throwable t) {
			// Trust no one. We got report about ClassNotFound from this place
			Log.severe(t, "Can't get adapter %s methods (possible sideness fail), bailing out", adapterClass);
			return result;
		}

		for (Method method : clsMethods) {
			try {
				LuaCallable callableAnn = method.getAnnotation(LuaCallable.class);
				if (callableAnn == null) continue;

				final MethodDeclaration decl = new MethodDeclaration(method, callableAnn, source);
				configureJavaArguments(decl);
				decl.validate();

				E exec = factory.createExecutor(method, decl);
				result.add(exec);
			} catch (Throwable e) {
				throw new MethodWrapException(method, e);
			}
		}

		return result;
	}
}