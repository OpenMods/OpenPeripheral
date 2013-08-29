package openperipheral.core.util;

import java.lang.reflect.Method;

public class CallWrapper<T> {

	public T call(Object target, String methodName, Object... args) throws CallFailureException {
		return call(target, new String[] { methodName }, args);
	}

	public T call(Object target, String[] methodNames, Object... args) throws CallFailureException {
		try {
			Method method = ReflectionHelper.getMethod(target.getClass(), methodNames, args.length);
			return (T)method.invoke(target, args);
		} catch (Exception e) {
			throw new CallFailureException();
		}
	}

	public static class CallFailureException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
}
