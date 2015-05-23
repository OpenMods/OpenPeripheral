package openperipheral.adapter.property;

import java.lang.reflect.Field;

import openperipheral.api.adapter.IPropertyCallback;

import com.google.common.base.Throwables;

public class FieldManipulatorProviders {

	public static final IFieldManipulator DEFAULT = new IFieldManipulator() {
		@Override
		public void setField(Object owner, Field field, Object value) {
			try {
				field.set(owner, value);
			} catch (Throwable t) {
				throw Throwables.propagate(t);
			}
		}

		@Override
		public Object getField(Object owner, Field field) {
			try {
				return field.get(owner);
			} catch (Throwable t) {
				throw Throwables.propagate(t);
			}
		}
	};

	public static final IFieldManipulator DELEGATING = new IFieldManipulator() {
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
		return isDelegating? DELEGATING : DEFAULT;
	}

}
