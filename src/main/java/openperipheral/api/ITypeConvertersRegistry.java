package openperipheral.api;

public interface ITypeConvertersRegistry extends IApiInterface {
	public void register(ITypeConverter converter);

	public Object fromLua(Object obj, Class<?> expected);

	public Object toLua(Object obj);
}
