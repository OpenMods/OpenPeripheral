package openperipheral.api.adapter.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * This annotation is used to mark arguments that should receive values provided in method call on script side.
 * Values will be converted to type of the argument.
 * </p>
 *
 * <p>
 * This annotation is also used for providing metadata for documentation
 * </p>
 *
 * @see Env
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Arg {
	/**
	 * This name will be visible in documentation program or in {@code .listMethods()} result.
	 * Value is mandatory, since information about argument name is not visible in runtime.
	 * This value has no effects in Java part
	 */
	String name();

	/**
	 * Short description, displayed by documentation program.
	 * This value is not used for validation and calling
	 */
	String description() default "";

	/**
	 * If this value is true, argument accepts {@code null} values ({@code nil} on scripting side).
	 * When nullable values are allowed, Java type of argument must not be primitive.
	 */
	boolean nullable() default false;
}
