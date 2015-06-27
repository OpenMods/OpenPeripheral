package openperipheral.api.property;

import java.lang.reflect.Field;

public interface ISinglePropertyListener {

	public void onPropertySet(Field field, Object value);

	public void onPropertyGet(Field field);

}
