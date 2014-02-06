package openperipheral.adapter;

import java.lang.reflect.Field;

public interface IPropertyCallback {
	public void setField(Object target, Field field, Object value);

	public Object getField(Object target, Field field);
}
