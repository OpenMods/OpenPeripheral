package openperipheral.api;

/**
 * API interface for registering adapters.
 *
 */
public interface IAdapterRegistry extends IApiInterface {
	/**
	 * Register adapter for peripherals (wrapped TileEntities).
	 */
	public boolean register(IPeripheralAdapter adapter);

	/**
	 * Register adapter for Lua objects (wrapped arbitrary objects, visible in Lua as objects with methods).
	 */
	public boolean register(IObjectAdapter adapter);

	/**
	 * Precalculate adapter for class. May be used for triggering error checking.
	 */
	public void registerInline(Class<?> cls);
}
