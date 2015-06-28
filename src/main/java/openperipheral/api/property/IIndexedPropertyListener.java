package openperipheral.api.property;

import java.lang.reflect.Field;

import openperipheral.api.adapter.IndexedCallbackProperty;
import openperipheral.api.adapter.IndexedProperty;

/**
 * Object implementing this interface will receive all calls to indexed properties.
 * This mechanism is independent from normal property behaviour, which will run normally (even callback ones)
 *
 * @see IndexedProperty
 * @see IndexedCallbackProperty
 */
public interface IIndexedPropertyListener {

	public void onPropertySet(Field field, Object key, Object value);

	public void onPropertyGet(Field field, Object key);

}
