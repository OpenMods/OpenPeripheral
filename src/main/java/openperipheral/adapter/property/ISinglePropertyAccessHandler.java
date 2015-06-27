package openperipheral.adapter.property;

import java.lang.reflect.Field;

import openperipheral.api.property.ISinglePropertyListener;

public interface ISinglePropertyAccessHandler {

	public static final ISinglePropertyAccessHandler IGNORE = new ISinglePropertyAccessHandler() {
		@Override
		public void onSet(Object owner, Object target, Field field, Object value) {}

		@Override
		public void onGet(Object owner, Object target, Field field) {}
	};

	public static final ISinglePropertyAccessHandler DELEGATE_TO_OWNER = new ISinglePropertyAccessHandler() {
		@Override
		public void onSet(Object owner, Object target, Field field, Object value) {
			((ISinglePropertyListener)owner).onPropertySet(field, value);
		}

		@Override
		public void onGet(Object owner, Object target, Field field) {
			((ISinglePropertyListener)owner).onPropertyGet(field);
		}
	};

	public void onSet(Object owner, Object target, Field field, Object value);

	public void onGet(Object owner, Object target, Field field);

}
