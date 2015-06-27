package openperipheral.api.property;

import java.lang.reflect.Field;

public interface IIndexedCustomProperty<K, V> {

	public V get(Object owner, Field field, K key);

	public void set(Object owner, Field field, K key, V value);

}
