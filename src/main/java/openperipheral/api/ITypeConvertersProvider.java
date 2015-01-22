package openperipheral.api;

public interface ITypeConvertersProvider extends IApiInterface {
	public ITypeConvertersRegistry getConverter(String architecture);
}
