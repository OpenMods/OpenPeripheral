package openperipheral.api.property;

import java.lang.reflect.Field;
import openperipheral.api.adapter.CallbackProperty;
import openperipheral.api.adapter.Property;

/**
 * When type implementing this interface is used with {@link Property}, all calls will be passed to this object.
 * Normal functionality will be overriden.
 *
 * <strong>Note:</strong> this interface does not work with {@link CallbackProperty}.
 */
public interface ISingleCustomProperty<V> {

	public V get(Object owner, Field field);

	public void set(Object owner, Field field, V value);
}
