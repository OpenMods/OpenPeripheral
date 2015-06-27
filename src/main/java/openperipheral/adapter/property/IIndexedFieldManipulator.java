package openperipheral.adapter.property;

import java.lang.reflect.Field;

public interface IIndexedFieldManipulator {
	public void setField(Object owner, Object target, Field field, Object index, Object value);

	public Object getField(Object owner, Object target, Field field, Object index);
}
