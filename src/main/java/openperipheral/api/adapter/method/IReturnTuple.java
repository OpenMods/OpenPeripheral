package openperipheral.api.adapter.method;

/**
 * <p>
 * Type used to return fixed number of values.
 * </p>
 * <p>
 * Instead of using this interface direction, create subclass with at least 2 type parameters.
 * </p>
 *
 * <p>
 * For default sub-types, see {@link IMultipleReturnsHelper}.
 * </p>
 *
 */
public interface IReturnTuple {
	public Object[] values();
}