package openperipheral.adapter.wrappers;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Set;

import openperipheral.api.Asynchronous;
import openperipheral.api.ExcludeArchitecture;

import com.google.common.collect.ImmutableSet;

public class MethodMetaExtractor {

	private static final boolean DEFAULT_ASYNC = false;

	private static final ImmutableSet<String> DEFAULT_BLACKLIST = ImmutableSet.of();

	private final boolean classIsAsync;

	private final Set<String> classExcludedArchitectures;

	private static boolean isAsynchronous(AnnotatedElement element, boolean defaultValue) {
		if (element == null) return defaultValue;
		Asynchronous async = element.getAnnotation(Asynchronous.class);
		return async != null? async.value() : defaultValue;
	}

	private static Set<String> getArchBlacklist(AnnotatedElement element, Set<String> defaultValue) {
		if (element == null) return defaultValue;
		ExcludeArchitecture blacklist = element.getAnnotation(ExcludeArchitecture.class);
		return blacklist != null? ImmutableSet.copyOf(blacklist.value()) : defaultValue;
	}

	public MethodMetaExtractor(Class<?> cls) {
		final Package pkg = cls.getPackage();

		this.classIsAsync = isAsynchronous(cls, DEFAULT_ASYNC);

		Set<String> pkgExcludedArchitectures = getArchBlacklist(pkg, DEFAULT_BLACKLIST);
		this.classExcludedArchitectures = getArchBlacklist(cls, pkgExcludedArchitectures);

	}

	public boolean isAsync(Method method) {
		return isAsynchronous(method, classIsAsync);
	}

	public Set<String> getExcludedArchitectures(Method method) {
		return getArchBlacklist(method, classExcludedArchitectures);
	}
}
