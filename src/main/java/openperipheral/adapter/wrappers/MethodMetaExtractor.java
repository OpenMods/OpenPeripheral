package openperipheral.adapter.wrappers;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Set;

import openperipheral.api.adapter.Asynchronous;
import openperipheral.api.adapter.ReturnSignal;
import openperipheral.api.architecture.ExcludeArchitecture;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

public class MethodMetaExtractor {

	private static final boolean DEFAULT_ASYNC = false;

	private static final ImmutableSet<String> DEFAULT_BLACKLIST = ImmutableSet.of();

	private final boolean classIsAsync;

	private final Optional<String> classReturnSignal;

	private final Set<String> classExcludedArchitectures;

	private static boolean isAsynchronous(AnnotatedElement element, boolean defaultValue) {
		if (element == null) return defaultValue;
		Asynchronous async = element.getAnnotation(Asynchronous.class);
		return async != null? async.value() : defaultValue;
	}

	private static Optional<String> getReturnSignal(AnnotatedElement element, Optional<String> defaultValue) {
		if (element == null) return defaultValue;
		final ReturnSignal ret = element.getAnnotation(ReturnSignal.class);
		return ret != null? Optional.of(ret.value()) : defaultValue;
	}

	private static Set<String> getArchBlacklist(AnnotatedElement element, Set<String> defaultValue) {
		if (element == null) return defaultValue;
		ExcludeArchitecture blacklist = element.getAnnotation(ExcludeArchitecture.class);
		return blacklist != null? ImmutableSet.copyOf(blacklist.value()) : defaultValue;
	}

	public MethodMetaExtractor(Class<?> cls) {
		final Package pkg = cls.getPackage();

		this.classIsAsync = isAsynchronous(cls, DEFAULT_ASYNC);

		this.classReturnSignal = getReturnSignal(cls, Optional.<String> absent());

		Set<String> pkgExcludedArchitectures = getArchBlacklist(pkg, DEFAULT_BLACKLIST);
		this.classExcludedArchitectures = getArchBlacklist(cls, pkgExcludedArchitectures);

	}

	public boolean isAsync(Method method) {
		return isAsynchronous(method, classIsAsync);
	}

	public Optional<String> getReturnSignal(Method method) {
		return getReturnSignal(method, classReturnSignal);
	}

	public Set<String> getExcludedArchitectures(Method method) {
		return getArchBlacklist(method, classExcludedArchitectures);
	}
}
