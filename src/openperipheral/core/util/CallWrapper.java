package openperipheral.core.util;

import java.lang.reflect.Method;

public class CallWrapper<T> {

	public T call(Object target, String methodName, Object... args) throws CallFailureException {
		return call(target, new String[] { methodName }, args);
	}

  @SuppressWarnings("rawtypes")
	public T call(Object target, String[] methodNames, Object... args) throws CallFailureException {
	  if (target instanceof Class) { // This is a static call
	    return explicitCall((Class)target, null, methodNames, args);
	  }else{
	     return explicitCall(target.getClass(), target, methodNames, args);
	  }
	}

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private T explicitCall(Class targetClass, Object target, String[] methodNames, Object... args) throws CallFailureException {
	    try {
	      Method method = ReflectionHelper.getMethod(targetClass, methodNames, args.length);
	      if (method == null){
	        throw new CallFailureException("Method not found");
	      }
	      return (T)method.invoke(target, args);
	    } catch (Exception e) {
	      throw new CallFailureException(e);
	    }
	  }

	public static class CallFailureException extends RuntimeException {
		public CallFailureException(Exception e) {
      super(e);
    }

    public CallFailureException(String message) {
      super(message);
    }

    private static final long serialVersionUID = 1L;
	}
}
