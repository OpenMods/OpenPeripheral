package openperipheral.api.adapter;

import java.lang.annotation.*;

/**
 * Method marked with this annotation will be called inside computer thread (instead of being called inside main thread, after world tick).
 * When used on class level, it will apply to every method, unless method declares it's own annotation.
 *
 * Behavior can be changed with {@link ReturnSignal}.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Asynchronous {
	boolean value() default true;
}
