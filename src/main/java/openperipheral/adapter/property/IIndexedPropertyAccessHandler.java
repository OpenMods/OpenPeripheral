package openperipheral.adapter.property;

import java.lang.reflect.Field;

import openperipheral.api.property.IIndexedPropertyListener;

public interface IIndexedPropertyAccessHandler {

	public static final IIndexedPropertyAccessHandler IGNORE = new IIndexedPropertyAccessHandler() {
		@Override
		public void onSet(Object owner, Object target, Field field, Object key, Object value) {}

		@Override
		public void onGet(Object owner, Object target, Field field, Object key) {}
	};

	public static final IIndexedPropertyAccessHandler DELEGATE_TO_OWNER = new IIndexedPropertyAccessHandler() {
		@Override
		public void onSet(Object owner, Object target, Field field, Object key, Object value) {
			((IIndexedPropertyListener)owner).onPropertySet(field, key, value);
		}

		@Override
		public void onGet(Object owner, Object target, Field field, Object key) {
			((IIndexedPropertyListener)owner).onPropertyGet(field, key);
		}
	};

	public void onSet(Object owner, Object target, Field field, Object key, Object value);

	public void onGet(Object owner, Object target, Field field, Object key);

}
