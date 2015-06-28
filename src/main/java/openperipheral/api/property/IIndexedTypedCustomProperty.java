package openperipheral.api.property;

import java.lang.reflect.Type;

/**
 * This interface can be used for properties that have custom conversion logic.
 *
 * @see IIndexedCustomProperty
 */
public interface IIndexedTypedCustomProperty<K, V> extends IIndexedCustomProperty<K, V> {
	/**
	 *
	 * @param key
	 *            'index' value, converted to type defined by generic paramter K
	 * @return type that should be used to convert value before passing to callback.
	 */
	public Type getType(Object key);
}
