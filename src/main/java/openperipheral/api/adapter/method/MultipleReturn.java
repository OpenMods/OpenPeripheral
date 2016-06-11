package openperipheral.api.adapter.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Used to mark methods for adapters that return multiple types.
 * When used, method should return {@link IMultiReturn}, Java collection or array.
 *
 * @see IMultiReturn
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MultipleReturn {}
