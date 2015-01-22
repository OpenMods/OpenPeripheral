package openperipheral.api;

import java.lang.annotation.*;

/**
 * Used to mark methods that should be visible in Lua.
 *
 * When used in inline adapters (defined inside class), accepted arguments are
 * <ul>
 * <li>Argument annotated with {@link Env} - for implementation specific details</li>
 * <li>Argument annotated with {@link Arg} - for Lua visible arguments</li>
 * </ul>
 *
 * When used in external adapters (defined in {@link IAdapter}, accepted arguments are
 * <ul>
 * <li>target - should be target class (see {@link IAdapter#getTargetClass()} or superclass</li>
 * <li>Argument annotated with {@link Env} - for implementation specific details</li>
 * <li>Argument annotated with {@link Arg} - for Lua visible arguments</li>
 * </ul>
 *
 * @see MultipleReturn
 * @see Asynchronous
 * @see Arg
 * @see Env
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LuaCallable {
	public static final String USE_METHOD_NAME = "[none set]";

	/**
	 * Name visible in Lua. Default will use Java name. More names can be defined with {@link Alias}
	 */
	String name() default USE_METHOD_NAME;

	String description() default "";

	/**
	 * List of types expected to be returned from call. Empty list marks no results. Using {@link LuaReturnType#VOID} in this list will cause error
	 */
	LuaReturnType[] returnTypes() default {};

	/**
	 * Should return values be validated using types from {@link #returnTypes()}
	 */
	boolean validateReturn() default true;
}
