package openperipheral.api.architecture;

import openperipheral.api.adapter.method.Env;

/**
 * Set of common methods from architectures. Used as argument of types marked with {@link Env}
 *
 */
public interface IArchitectureAccess {

	public String architecture();

	public String callerName();

	public String peripheralName();

	/**
	 * Checks if peripheral is attached to valid, running computer.
	 */
	public boolean canSignal();

	public boolean signal(String name, Object... args);

	/**
	 * Convert object to Lua object with callable methods
	 */
	public Object wrapObject(Object target);
}
