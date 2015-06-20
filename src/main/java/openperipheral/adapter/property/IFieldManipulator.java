package openperipheral.adapter.property;

import java.lang.reflect.Field;

public interface IFieldManipulator {
	public void setField(Object target, Field field, Object value);

	public Object getField(Object target, Field field);
}
