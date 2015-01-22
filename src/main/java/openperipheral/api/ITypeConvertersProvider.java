package openperipheral.api;

/**
 * Used for getting architecture specific {@link ITypeConvertersRegistry}.
 *
 * @see Constants
 *
 */
public interface ITypeConvertersProvider extends IApiInterface {
	public ITypeConvertersRegistry getConverter(String architecture);
}
