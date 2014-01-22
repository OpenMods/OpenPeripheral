package openperipheral.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

public class ReflectionHelper {

	private static class NullMarker {
		public final Class<?> cls;

		private NullMarker(Class<?> cls) {
			this.cls = cls;
		}
	}

	private static class TypeMarker {
		public final Class<?> cls;
		public final Object value;

		private TypeMarker(Class<?> cls, Object value) {
			this.cls = cls;
			this.value = value;
		}
	}

	public static Object nullValue(Class<?> cls) {
		return new NullMarker(cls);
	}

	public static Object typed(Object value, Class<?> cls) {
		return new TypeMarker(cls, value);
	}

	public static Object primitive(int value) {
		return new TypeMarker(int.class, value);
	}

	public static Object primitive(boolean value) {
		return new TypeMarker(boolean.class, value);
	}

	public static Object getProperty(Class<?> klazz, Object instance, String... fields) {
		Field field = getField(klazz == null? instance.getClass() : klazz, fields);
		Preconditions.checkNotNull(field, "Fields %s not found", Arrays.toString(fields));
		try {
			return field.get(instance);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	public static Object getProperty(String className, Object instance, String... fields) {
		return getProperty(getClass(className), instance, fields);
	}

	public static <T> T callStatic(Class<?> klazz, String methodName, Object... args) {
		return call(klazz, null, ArrayUtils.toArray(methodName), args);
	}

	public static <T> T call(Object instance, String methodName, Object... args) {
		return call(instance.getClass(), instance, ArrayUtils.toArray(methodName), args);
	}

	@SuppressWarnings("unchecked")
	public static <T> T call(Class<?> klazz, Object instance, String[] methodNames, Object... args) {
		Method m = getMethod(klazz, methodNames, args);
		Preconditions.checkNotNull(m, "Method %s not found", Arrays.toString(methodNames));

		for (int i = 0; i < args.length; i++) {
			final Object arg = args[i];
			if (arg instanceof NullMarker) args[i] = null;
			if (arg instanceof TypeMarker) args[i] = ((TypeMarker)arg).value;
		}

		m.setAccessible(true);
		try {
			return (T)m.invoke(instance, args);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	public static Method getMethod(Class<?> klazz, String[] methodNames, Object... args) {
		if (klazz == null) return null;
		Class<?> argTypes[] = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			final Object arg = args[i];
			if (arg instanceof NullMarker) argTypes[i] = ((NullMarker)arg).cls;
			else if (arg instanceof TypeMarker) argTypes[i] = ((TypeMarker)arg).cls;
			else argTypes[i] = arg.getClass();
		}

		for (String name : methodNames) {
			Method result = getDeclaredMethod(klazz, name, argTypes);
			if (result != null) return result;
		}
		return null;
	}

	public static Method getMethod(Class<?> klazz, String[] methodNames, Class<?>... types) {
		if (klazz == null) return null;
		for (String name : methodNames) {
			Method result = getDeclaredMethod(klazz, name, types);
			if (result != null) return result;
		}
		return null;
	}

	public static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>[] argsTypes) {
		while (clazz != null) {
			try {
				return clazz.getDeclaredMethod(name, argsTypes);
			} catch (NoSuchMethodException e) {} catch (Exception e) {
				throw Throwables.propagate(e);
			}
			clazz = clazz.getSuperclass();
		}
		return null;
	}

	public static List<Method> getAllMethods(Class<?> clazz) {
		List<Method> methods = Lists.newArrayList();
		while (clazz != null) {
			for (Method m : clazz.getDeclaredMethods())
				methods.add(m);
			clazz = clazz.getSuperclass();
		}
		return methods;

	}

	public static Field getField(Class<?> klazz, String... fields) {
		for (String field : fields) {
			Class<?> current = klazz;
			while (current != null) {
				try {
					Field f = current.getDeclaredField(field);
					f.setAccessible(true);
					return f;
				} catch (NoSuchFieldException e) {} catch (Exception e) {
					throw Throwables.propagate(e);
				}
				current = current.getSuperclass();
			}
		}
		return null;
	}

	public static Class<?> getClass(String className) {
		if (Strings.isNullOrEmpty(className)) return null;
		try {
			return Class.forName(className);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}
}
