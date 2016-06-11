package openperipheral.adapter;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.lang.reflect.AnnotatedElement;
import java.util.Set;
import openperipheral.api.adapter.Asynchronous;
import openperipheral.api.adapter.ReturnSignal;
import openperipheral.api.architecture.ExcludeArchitecture;
import openperipheral.api.architecture.FeatureGroup;

public class AnnotationMetaExtractor {

	private static final boolean DEFAULT_ASYNC = false;

	private static final Set<String> DEFAULT_BLACKLIST = ImmutableSet.of();

	private static final Set<String> DEFAULT_FEATURE_GROUPS = ImmutableSet.of();

	private final boolean classIsAsync;

	private final Optional<String> classReturnSignal;

	private final Set<String> classExcludedArchitectures;

	private final Set<String> classFeatureGroups;

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

	private static Set<String> getFeatureGroup(AnnotatedElement element, Set<String> prevValue) {
		if (element == null) return prevValue;
		FeatureGroup fg = element.getAnnotation(FeatureGroup.class);
		return (fg != null)? Sets.union(prevValue, Sets.newHashSet(fg.value())) : prevValue;
	}

	public AnnotationMetaExtractor(Class<?> cls) {
		final Package pkg = cls.getPackage();

		this.classIsAsync = isAsynchronous(cls, DEFAULT_ASYNC);

		this.classReturnSignal = getReturnSignal(cls, Optional.<String> absent());

		Set<String> pkgExcludedArchitectures = getArchBlacklist(pkg, DEFAULT_BLACKLIST);
		this.classExcludedArchitectures = getArchBlacklist(cls, pkgExcludedArchitectures);

		Set<String> pkgFeatureGroups = getFeatureGroup(pkg, DEFAULT_FEATURE_GROUPS);
		this.classFeatureGroups = getFeatureGroup(cls, pkgFeatureGroups);
	}

	public boolean isAsync(AnnotatedElement element) {
		return isAsynchronous(element, classIsAsync);
	}

	public Optional<String> getReturnSignal(AnnotatedElement element) {
		return getReturnSignal(element, classReturnSignal);
	}

	public Set<String> getExcludedArchitectures(AnnotatedElement element) {
		return getArchBlacklist(element, classExcludedArchitectures);
	}

	public Set<String> getFeatureGroups(AnnotatedElement element) {
		return getFeatureGroup(element, classFeatureGroups);
	}
}
