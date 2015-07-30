package openperipheral.api.architecture;

import openperipheral.api.Constants;
import openperipheral.api.adapter.method.Env;
import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IConverterManager;
import openperipheral.api.helpers.Index;

/**
 * Set of common methods from architectures. Used as argument of types marked with {@link Env}.
 *
 * @see Constants#ARG_ARCHITECTURE
 *
 */
public interface IArchitecture {

	public String architecture();

	/**
	 * Convert object to Lua object with callable methods
	 */
	public Object wrapObject(Object target);

	/**
	 * Creates new index with offset native for this architecture (usually 1 for Lua based architectures)
	 */
	public Index createIndex(int value);

	/**
	 * Return type converter.
	 * Alternative to {@code IConverterManager.getConverter(architecture())}
	 *
	 * @see IConverterManager#getConverter(String)
	 */
	public IConverter getConverter();
}
