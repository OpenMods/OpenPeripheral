package openperipheral.api;

/**
 * This class is used to access instancef of API interfaces (marked with {@link IApiInterface}).
 *
 * For alternative method, see {@link ApiHolder}.
 */
public class ApiAccess {
	public static final String API_VERSION = "$OP-API-VERSION$";

	public interface ApiProvider {
		public <T extends IApiInterface> T getApi(Class<T> cls);

		public <T extends IApiInterface> boolean isApiPresent(Class<T> cls);
	}

	private ApiAccess() {}

	private static ApiProvider provider;

	// OpenPeripheralCore will use this method to provide actual implementation
	public static void init(ApiProvider provider) {
		if (ApiAccess.provider != null) throw new IllegalStateException("API already initialized");
		ApiAccess.provider = provider;
	}

	/**
	 * @deprecated Use {@link ApiHolder}.
	 */
	@Deprecated
	public static <T extends IApiInterface> T getApi(Class<T> cls) {
		if (provider == null) throw new IllegalStateException("API not initialized");
		return provider.getApi(cls);
	}

	public static <T extends IApiInterface> boolean isApiPresent(Class<T> cls) {
		if (provider == null) throw new IllegalStateException("API not initialized");
		return provider.isApiPresent(cls);
	}
}
