package openperipheral.api.adapter;

import java.lang.reflect.Field;

/**
 *
 * Interface used to receive callback from generated accessors.
 *
 * @see IndexedCallbackProperty
 */
public interface IIndexedPropertyCallback {
	/**
	 * Called when user calls setter. It's up to implementation to actually set field value.
	 * Not called for read only fields.
	 *
	 * @param field
	 *            field annotated with {@link IndexedCallbackProperty}
	 * @param index
	 *            value used as index (second parameter), already converted to proper type
	 * @param value
	 *            new field value, already converted to proper type
	 */
	public void setField(Field field, Object index, Object value);

	/**
	 * Called when user calls getter.
	 *
	 * @param field
	 *            field annotated with {@link IndexedCallbackProperty}
	 * @param index
	 *            value used as index (second parameter), already converted to proper type
	 * @return raw value
	 */
	public Object getField(Field field, Object index);
}
