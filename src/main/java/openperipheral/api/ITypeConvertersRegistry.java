package openperipheral.api;

public interface ITypeConvertersRegistry extends IApiInterface {
	public void register(ITypeConverter converter);

	/**
	 * Register type that should be left unconverted and passed to Lua
	 */
	public void registerIgnored(Class<?> ignored, boolean includeSubclasses);

	public Object fromLua(Object obj, Class<?> expected);

	public Object toLua(Object obj);
}
