package openperipheral.api.property;

import java.lang.reflect.Field;

public interface ISingleCustomProperty<V> {

	public V get(Object owner, Field field);

	public void set(Object owner, Field field, V value);
}
