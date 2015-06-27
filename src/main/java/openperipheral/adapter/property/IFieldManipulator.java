package openperipheral.adapter.property;

import java.lang.reflect.Field;

public interface IFieldManipulator {
	public void setField(Object owner, Object target, Field field, Object value);

	public Object getField(Object owner, Object target, Field field);
}
