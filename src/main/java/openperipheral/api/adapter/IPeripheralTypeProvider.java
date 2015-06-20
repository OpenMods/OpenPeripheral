package openperipheral.api.adapter;

import openperipheral.api.IApiInterface;

/**
 * This interface is used for giving wrapper peripheral user friendly names.
 * Once set, name will be persisted between game restarts.
 * Calling this method can't change name of peripherals already attached to computers.
 */
public interface IPeripheralTypeProvider extends IApiInterface {

	public void setType(Class<?> cls, String type);

	public String getType(Class<?> cls);

	/**
	 * Get proposed type name for object
	 */
	public String generateType(Object target);
}
