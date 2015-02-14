package openperipheral.interfaces.oc.asm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Set;

import li.cil.oc.api.network.ManagedEnvironment;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.composed.IndexedMethodMap;

import com.google.common.base.Throwables;

public class EnvironmentFactory {

	private final EnvironmentCodeGenerator generator = new EnvironmentCodeGenerator();

	private static Method defineClass;

	public Class<? extends ManagedEnvironment> generateEnvironment(String name, Class<?> targetClass, Set<Class<?>> exposedInterfaces, IndexedMethodMap methods) {
		try {
			byte[] bytes = generator.generate(name, targetClass, exposedInterfaces, methods);
			@SuppressWarnings("unchecked")
			final Class<? extends ManagedEnvironment> result = (Class<? extends ManagedEnvironment>)defineClass(name, bytes);
			setMethodsField(methods.getMethods(), result);
			return result;
		} catch (Throwable t) {
			throw Throwables.propagate(t);
		}
	}

	private static void setMethodsField(IMethodExecutor[] methods, final Class<?> result) throws Exception {
		Field methodsField = result.getDeclaredField(EnvironmentBase.METHODS_FIELD);
		methodsField.setAccessible(true);
		methodsField.set(null, methods);
	}

	private Class<?> defineClass(String name, byte[] bytes) throws Exception {
		final Class<? extends EnvironmentFactory> ownClass = getClass();
		final ClassLoader cl = ownClass.getClassLoader();
		final ProtectionDomain pd = ownClass.getProtectionDomain();

		if (defineClass == null) {
			defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class, ProtectionDomain.class);
			defineClass.setAccessible(true);
		}

		return (Class<?>)defineClass.invoke(cl, name, bytes, 0, bytes.length, pd);
	}

}
