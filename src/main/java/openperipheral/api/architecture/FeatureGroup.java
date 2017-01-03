package openperipheral.api.architecture;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Feature groups allow to control what methods will be visible to architecture.
 * Use {@link IFeatureGroupManager} to blacklist groups for selected architectures.
 * If any of declared groups is banned from architecture, method will be excluded from generated list.
 *
 * Groups are collected on every level, from package to method or field.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
public @interface FeatureGroup {
	public String[] value();
}
