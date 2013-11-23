package openperipheral.util;

import java.lang.reflect.Method;

public class CallWrapper<T> {
	public T call(Object target, String methodName, Object... args) throws CallFailureException {
		return call(target, new String[] { methodName }, args);
	}

	public T call(Object target, String[] methodNames, Object... args) throws CallFailureException {
		if (target instanceof Class) { // This is a static call
			return explicitCall((Class<?>)target, null, methodNames, args);
		}
		return explicitCall(target.getClass(), target, methodNames, args);
	}

	@SuppressWarnings("unchecked")
	private T explicitCall(Class<?> targetClass, Object target, String[] methodNames, Object... args) throws CallFailureException {
		try {
			Method method = ReflectionHelper.getMethod(targetClass, methodNames, args.length);
			if (method == null) {
				throw new CallFailureException("Method not found");
			}
			return (T) method.invoke(target, args);
		} catch (Exception e) {
			throw new CallFailureException(e);
		}
	}

	public static class CallFailureException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public CallFailureException(Exception e) {
			super(e);
		}

		public CallFailureException(String message) {
			super(message);
		}
	}
}
