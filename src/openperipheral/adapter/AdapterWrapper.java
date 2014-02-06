package openperipheral.adapter;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;

import openmods.Log;
import openperipheral.api.*;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public abstract class AdapterWrapper<E extends IMethodExecutor> {
	protected static boolean isFreeform(AnnotatedElement element, boolean defaultValue) {
		Freeform freeform = element.getAnnotation(Freeform.class);
		return freeform != null? freeform.value() : defaultValue;
	}

	public final List<E> methods;
	public final Class<?> targetCls;
	public final Class<?> adapterClass;

	protected AdapterWrapper(Class<?> adapterClass, Class<?> targetClass) {
		this.adapterClass = adapterClass;
		this.targetCls = targetClass;
		this.methods = ImmutableList.copyOf(buildMethodList());
	}

	protected abstract List<E> buildMethodList();

	protected void namesFromAnnotation(Prefixed prefixes, MethodDeclaration decl) {
		int i = 0;
		for (String name : prefixes.value())
			decl.nameJavaArg(i++, name);
	}

	protected abstract void validateArgTypes(MethodDeclaration decl);

	protected abstract void nameDefaultParameters(MethodDeclaration decl);

	protected interface MethodExecutorFactory<E extends IMethodExecutor> {
		public E createExecutor(Method method, MethodDeclaration decl);
	}

	protected MethodDeclaration createDeclaration(Method method) {
		LuaMethod methodAnn = method.getAnnotation(LuaMethod.class);
		if (methodAnn != null) return new MethodDeclaration(method, methodAnn);

		LuaCallable callableAnn = method.getAnnotation(LuaCallable.class);
		if (callableAnn != null) return new MethodDeclaration(method, callableAnn);

		return null;
	}

	protected List<E> buildMethodList(boolean defaultIsFreeform, MethodExecutorFactory<E> factory) {
		List<E> result = Lists.newArrayList();
		final boolean clsIsFreeform = isFreeform(adapterClass, defaultIsFreeform);
		final Prefixed classPrefixes = adapterClass.getAnnotation(Prefixed.class);

		Method[] clsMethods;
		try {
			clsMethods = adapterClass.getMethods();
		} catch (Throwable t) {
			// Trust no one. We got report about ClassNotFound from this place
			Log.severe(t, "Can't get adapter %s methods (possible sideness fail), bailing out", adapterClass);
			return result;
		}

		for (Method method : clsMethods) {
			MethodDeclaration decl = createDeclaration(method);

			if (decl == null) continue;

			E exec = factory.createExecutor(method, decl);

			final Prefixed methodPrefixes = method.getAnnotation(Prefixed.class);
			final Prefixed actualPrefixes = methodPrefixes != null? methodPrefixes : classPrefixes;
			if (!isFreeform(method, clsIsFreeform)) {
				if (actualPrefixes == null) nameDefaultParameters(decl);
				else namesFromAnnotation(actualPrefixes, decl);
			} else Preconditions.checkState(methodPrefixes == null, "Method '%s' has mutually exclusive annotations @Prefixed and @Freeform");

			decl.validate();
			validateArgTypes(decl);

			result.add(exec);
		}

		return result;
	}

}