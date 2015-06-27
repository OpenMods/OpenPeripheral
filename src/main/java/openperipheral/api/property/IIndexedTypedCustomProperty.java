package openperipheral.api.property;

import java.lang.reflect.Type;

public interface IIndexedTypedCustomProperty<K, V> extends IIndexedCustomProperty<K, V> {
	public Type getType(Object key);
}
