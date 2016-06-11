package openperipheral.api.adapter.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import openperipheral.api.Constants;

/**
 * Used to mark method arguments as receivers of instance of specific variable
 * Available variable names and expected types of argument depend on context. See {@link Constants} for possible values and type.
 *
 *
 * Selecting some values will exclude method from not supporting architectures (e.g. using {@link Constants#ARG_COMPUTER} will hide this method from OpenComputers).
 *
 * @see Arg
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Env {
	public String value();
}
