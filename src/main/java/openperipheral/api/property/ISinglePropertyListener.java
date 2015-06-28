package openperipheral.api.property;

import java.lang.reflect.Field;

import openperipheral.api.adapter.CallbackProperty;
import openperipheral.api.adapter.Property;

/**
 * Object implementing this interface will receive all calls to single properties.
 * This mechanism is independent from normal property behaviour, which will run normally (even callback ones)
 *
 * @see Property
 * @see CallbackProperty
 */
public interface ISinglePropertyListener {

	public void onPropertySet(Field field, Object value);

	public void onPropertyGet(Field field);

}
