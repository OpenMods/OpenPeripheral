package openperipheral.api.property;

import java.lang.reflect.Type;

public interface ISingleTypedCustomProperty<V> extends ISingleCustomProperty<V> {
	public Type getType();
}
