package openperipheral.api.adapter;

import java.lang.annotation.*;

/**
 * Changes behavior of function to following:
 * <ol>
 * <li>call returns immediately with callbackId</li>
 * <li>method is scheduled for execution - either in main thread (default) or in helper thread (when marked with {@link Asynchronous})</li>
 * <li>if function finished successful it pushes signal with:
 * <ol>
 * <li>id given in value of this annotation</li>
 * <li>callbackId</li>
 * <li>indication of success: {@literal true}</li>
 * <li>values returned from function</li>
 * </ol>
 * <li>if function finished with exception it pushes signal with:
 * <ol>
 * <li>id given in value of this annotation</li>
 * <li>callbackId</li>
 * <li>indication of failure: {@literal false}</li>
 * <li>exception message</li>
 * </ol>
 * </li>
 * </ol>
 *
 * This allows to easily implement interfaces that require multiple synchronized calls in the same tick or long processing time.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface ReturnSignal {
	public String value() default "op_task_done";
}
