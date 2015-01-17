package openperipheral.adapter;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import openperipheral.api.Asynchronous;

public class AsyncChecker {

	private final boolean classIsAsync;

	private static boolean isAsynchronous(AnnotatedElement element, boolean defaultValue) {
		Asynchronous async = element.getAnnotation(Asynchronous.class);
		return async != null? async.value() : defaultValue;
	}

	public AsyncChecker(Class<?> cls) {
		// TODO default based on type, once available
		classIsAsync = isAsynchronous(cls, false);
	}

	public boolean isAsync(Method method) {
		return isAsynchronous(method, classIsAsync);
	}
}
