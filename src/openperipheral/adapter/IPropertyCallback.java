package openperipheral.adapter;

import java.lang.reflect.Field;

public interface IPropertyCallback {
	public void setField(Field field, Object value);

	public Object getField(Field field);
}
