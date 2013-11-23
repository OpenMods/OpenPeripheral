package openperipheral.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import openperipheral.TypeConversionRegistry;

public class ReflectionHelper {

	public static void setProperty(Class<?> klazz, Object instance, Object value, String... fields) {
		Field field = getField(klazz == null? instance.getClass() : klazz, fields);
		if (field != null) {
			try {
				field.set(instance, TypeConversionRegistry.fromLua(value, field.getType()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void setProperty(String className, Object instance, Object value, String... fields) {
		setProperty(getClass(className), instance, value, fields);
	}

	public static Object getProperty(Class<?> klazz, Object instance, String... fields) {
		Field field = getField(klazz == null? instance.getClass() : klazz, fields);
		if (field != null) {
			try {
				return field.get(instance);
			} catch (Exception e) {}
		}
		return null;
	}

	public static Object getProperty(String className, Object instance, String... fields) {
		return getProperty(getClass(className), instance, fields);
	}

	public static Object callMethod(boolean replace, String className, Object instance, String[] methodNames, Object... args) throws Exception {
		return callMethod(replace, getClass(className), instance, methodNames, args);
	}

	public static Object callMethod(String className, Object instance, String[] methodNames, Object... args) throws Exception {
		return callMethod(getClass(className), instance, methodNames, args);
	}

	public static Object callMethod(boolean replace, Class<?> klazz, Object instance, String[] methodNames, Object... args) throws Exception {
		Method m = getMethod(klazz == null? instance.getClass() : klazz, methodNames, args.length);
		if (m != null) {
			Class<?>[] types = m.getParameterTypes();
			List<Object> argumentList = Arrays.asList(args);
			if (replace) {
				for (int i = 0; i < argumentList.size(); i++) {
					Object newType = TypeConversionRegistry.fromLua(argumentList.get(i), types[i]);
					argumentList.set(i, newType);
				}
			}
			Object response = m.invoke(instance, argumentList.toArray(new Object[args.length]));
			return response;
		}
		return null;
	}

	public static Object callMethod(Class<?> klazz, Object instance, String[] methodNames, Object... args) throws Exception {
		return callMethod(true, klazz, instance, methodNames, args);
	}

	public static Method getMethod(Class<?> klazz, String[] methodNames, int argCount) {
		if (klazz == null) { return null; }
		for (String method : methodNames) {
			try {
				for (Method m : getAllMethods(klazz)) {
					if (m.getName().equals(method) && (argCount == -1 || m.getParameterTypes().length == argCount)) {
						m.setAccessible(true);
						return m;
					}
				}
			} catch (Exception e) {}
		}
		return null;
	}

	public static Method[] getAllMethods(Class<?> clazz) {
		ArrayList<Method> methods = new ArrayList<Method>();
		while (clazz != null) {
			for (Method m : clazz.getDeclaredMethods()) {
				methods.add(m);
			}
			clazz = clazz.getSuperclass();
		}
		return methods.toArray(new Method[methods.size()]);

	}

	public static Field getField(Class<?> klazz, String... fields) {
		for (String field : fields) {
			Class<?> current = klazz;
			while (current != null) {
				try {
					Field f = current.getDeclaredField(field);
					f.setAccessible(true);
					return f;
				} catch (Exception e) {}
				current = current.getSuperclass();
			}
		}
		return null;
	}

	public static Class<?> getClass(String className) {
		if (className == null || className.isEmpty()) { return null; }
		try {
			return Class.forName(className);
		} catch (Exception e) {}
		return null;
	}

}
