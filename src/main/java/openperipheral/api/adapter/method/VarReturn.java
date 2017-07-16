package openperipheral.api.adapter.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Used to mark methods for adapters that return multiple values.
 * When used, method should return Java collection or array.
 * Methods with this annotation will have multiple returns instead of single table one.
 *
 * When size of return values is know, use IReturnTuple*
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface VarReturn {}
