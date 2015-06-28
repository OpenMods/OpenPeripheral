package openperipheral.api.property;

import java.lang.reflect.Type;

/**
 * This interface can be used for properties that have custom conversion logic
 *
 * @see ISingleCustomProperty
 */
public interface ISingleTypedCustomProperty<V> extends ISingleCustomProperty<V> {
	public Type getType();
}
