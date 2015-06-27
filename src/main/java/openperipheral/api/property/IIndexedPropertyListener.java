package openperipheral.api.property;

import java.lang.reflect.Field;

public interface IIndexedPropertyListener {

	public void onPropertySet(Field field, Object key, Object value);

	public void onPropertyGet(Field field, Object key);

}
