package openperipheral.api.property;

import java.lang.reflect.Field;

import openperipheral.api.adapter.IndexedCallbackProperty;
import openperipheral.api.adapter.IndexedProperty;

/**
 * When type implementing this interface is used with {@link IndexedProperty}, all calls will be passed to this object.
 * Normal functionality will be overriden.
 *
 * <strong>Note:</strong> this interface does not work with {@link IndexedCallbackProperty}.
 */
public interface IIndexedCustomProperty<K, V> {

	public V get(Object owner, Field field, K key);

	public void set(Object owner, Field field, K key, V value);

}
