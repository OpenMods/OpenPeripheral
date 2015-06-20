package openperipheral.adapter.property;

import java.lang.reflect.Field;

import openperipheral.api.adapter.IPropertyCallback;

import com.google.common.base.Throwables;

public class SingleManipulatorProvider {

	@SuppressWarnings("unchecked")
	public static <T> T getContents(Object target, Field field) {
		try {
			return (T)field.get(target);
		} catch (Throwable t) {
			throw Throwables.propagate(t);
		}
	}

	public static void setContents(Object owner, Field field, Object value) {
		try {
			field.set(owner, value);
		} catch (Throwable t) {
			throw Throwables.propagate(t);
		}
	}

	public static final IFieldManipulator FIELD_MANIPULATOR = new IFieldManipulator() {
		@Override
		public void setField(Object owner, Field field, Object value) {
			setContents(owner, field, value);
		}

		@Override
		public Object getField(Object owner, Field field) {
			return getContents(owner, field);
		}
	};

	public static final IFieldManipulator DELEGATING_MANIPULATOR = new IFieldManipulator() {
		@Override
		public void setField(Object target, Field field, Object value) {
			((IPropertyCallback)target).setField(field, value);
		}

		@Override
		public Object getField(Object target, Field field) {
			return ((IPropertyCallback)target).getField(field);
		}
	};

	public static IFieldManipulator getProvider(boolean isDelegating) {
		return isDelegating? DELEGATING_MANIPULATOR : FIELD_MANIPULATOR;
	}

}
