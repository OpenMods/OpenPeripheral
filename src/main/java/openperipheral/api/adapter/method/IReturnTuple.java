package openperipheral.api.adapter.method;

/**
 * Type used to return fixed number of values.<br/>
 * Instead of using this interface direction, create subclass with at least 2 type parameters.<br/>
 *
 * For default sub-types, see {@link IMultipleReturnsHelper}.
 *
 */
public interface IReturnTuple {
	public Object[] values();
}