package openperipheral.adapter;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import openmods.Log;
import openmods.reflection.ReflectionHelper;
import openperipheral.adapter.method.MethodDeclaration;
import openperipheral.adapter.method.MethodDeclaration.CallWrap;
import openperipheral.api.*;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.*;

public abstract class AdapterWrapper<E extends IMethodExecutor> implements IAdapterMethodsList<E> {

	private static boolean isFreeform(AnnotatedElement element, boolean defaultValue) {
		Freeform freeform = element.getAnnotation(Freeform.class);
		return freeform != null? freeform.value() : defaultValue;
	}

	private static String[] getPrefixes(AnnotatedElement element, String[] defaultValue) {
		if (element == null) return defaultValue; // possible for net.minecraft
													// classes
		Prefixed prefixed = element.getAnnotation(Prefixed.class);
		return (prefixed != null)? prefixed.value() : defaultValue;
	}

	protected final List<E> methods;
	protected final Class<?> targetCls;
	protected final Class<?> adapterClass;
	protected final String source;

	protected AdapterWrapper(Class<?> adapterClass, Class<?> targetClass, String source) {
		this.adapterClass = adapterClass;
		this.targetCls = targetClass;
		this.source = source;
		this.methods = ImmutableList.copyOf(buildMethodList());
	}

	@Override
	public List<E> listMethods() {
		return methods;
	}

	@Override
	public Class<?> getTargetClass() {
		return targetCls;
	}

	@Override
	public String describeType() {
		return "wrapped (source: " + adapterClass.toString() + ")";
	}

	protected abstract List<E> buildMethodList();

	protected void namesFromAnnotation(String[] prefixes, MethodDeclaration decl) {
		int i = 0;
		for (String name : prefixes)
			decl.nameJavaArg(i++, name);
	}

	protected abstract void validateArgTypes(MethodDeclaration decl);

	protected abstract void nameDefaultParameters(MethodDeclaration decl);

	protected interface MethodExecutorFactory<E extends IMethodExecutor> {
		public E createExecutor(Method method, MethodDeclaration decl, Map<String, Method> proxyArgs);
	}

	protected MethodDeclaration createDeclaration(Method method) {
		LuaCallable callableAnn = method.getAnnotation(LuaCallable.class);
		if (callableAnn != null) return new MethodDeclaration(method, callableAnn, source);

		return null;
	}

	private void addProxyArgs(Map<String, Method> result, String defaultName, Class<?>[] defaultArgs, ProxyArg arg) {
		String name = arg.argName();

		String[] names = arg.methodNames();
		if (names.length == 0) names = new String[] { defaultName };

		Class<?> args[] = arg.args();
		if (args.length == 1 && args[0] == void.class) args = defaultArgs;

		Method proxiedMethod = ReflectionHelper.getMethod(targetCls, names, args);
		Preconditions.checkNotNull(proxiedMethod, "Can find proxy argument '%s' for method %s %s in class (adapter: %s)",
				name, targetCls, Arrays.toString(names), Arrays.toString(args), targetCls, adapterClass);
		proxiedMethod.setAccessible(true);
		Method prev = result.put(name, proxiedMethod);
		Preconditions.checkState(prev == null, "Duplicated proxy arg name '%s' in adapter '%s'", name, adapterClass);
	}

	protected static ICallableWithArgs createProxy(final Object target, final Method method) {
		return new ICallableWithArgs() {
			@SuppressWarnings("unchecked")
			@Override
			public <T> T call(Object... args) {
				try {
					return (T)method.invoke(target, args);
				} catch (Throwable t) {
					throw Throwables.propagate(t);
				}
			}
		};
	}

	protected static CallWrap nameAdapterMethods(Object target, Map<String, Method> proxyArgs, CallWrap wrap) {
		for (Map.Entry<String, Method> e : proxyArgs.entrySet())
			wrap.setJavaArg(e.getKey(), createProxy(target, e.getValue()));

		return wrap;
	}

	protected List<E> buildMethodList(boolean defaultIsFreeform, MethodExecutorFactory<E> factory) {
		List<E> result = Lists.newArrayList();
		final boolean clsIsFreeform = isFreeform(adapterClass, defaultIsFreeform);

		final String[] packagePrefixes = getPrefixes(adapterClass.getPackage(), null);
		final String[] classPrefixes = getPrefixes(adapterClass, packagePrefixes);

		Method[] clsMethods;
		try {
			clsMethods = adapterClass.getDeclaredMethods();
		} catch (Throwable t) {
			// Trust no one. We got report about ClassNotFound from this place
			Log.severe(t, "Can't get adapter %s methods (possible sideness fail), bailing out", adapterClass);
			return result;
		}

		for (Method method : clsMethods) {
			MethodDeclaration decl = createDeclaration(method);

			if (decl == null) continue;

			Map<String, Method> allProxyArgs = Maps.newHashMap();

			Class<?>[] luaArgs = decl.getLuaArgTypes();
			final ProxyArg proxyArg = method.getAnnotation(ProxyArg.class);
			if (proxyArg != null) addProxyArgs(allProxyArgs, method.getName(), luaArgs, proxyArg);

			final ProxyArgs proxyArgs = method.getAnnotation(ProxyArgs.class);
			if (proxyArgs != null) for (ProxyArg arg : proxyArgs.value())
				addProxyArgs(allProxyArgs, method.getName(), luaArgs, arg);

			E exec = factory.createExecutor(method, decl, ImmutableMap.copyOf(allProxyArgs));

			if (!isFreeform(method, clsIsFreeform)) {
				final String[] methodPrefixes = getPrefixes(method, classPrefixes);
				if (methodPrefixes != null) namesFromAnnotation(methodPrefixes, decl);
				else nameDefaultParameters(decl);
			}

			validateArgTypes(decl);
			for (String proxyArgName : allProxyArgs.keySet())
				decl.declareJavaArgType(proxyArgName, ICallableWithArgs.class);

			decl.validate();

			result.add(exec);
		}

		return result;
	}

}