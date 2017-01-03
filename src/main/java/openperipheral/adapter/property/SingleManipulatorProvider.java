package openperipheral.adapter.property;

import com.google.common.base.Preconditions;
import java.lang.reflect.Field;
import openperipheral.api.adapter.IPropertyCallback;
import openperipheral.api.property.ISingleCustomProperty;

public class SingleManipulatorProvider {

	public static final IFieldManipulator FIELD_MANIPULATOR = new IFieldManipulator() {
		@Override
		public void setField(Object owner, Object target, Field field, Object value) {
			PropertyUtils.setContents(owner, field, value);
		}

		@Override
		public Object getField(Object owner, Object target, Field field) {
			return target;
		}
	};

	public static final IFieldManipulator OWNER_DELEGATING_MANIPULATOR = new IFieldManipulator() {
		@Override
		public void setField(Object owner, Object target, Field field, Object value) {
			((IPropertyCallback)owner).setField(field, value);
		}

		@Override
		public Object getField(Object owner, Object target, Field field) {
			return ((IPropertyCallback)owner).getField(field);
		}
	};

	public static final IFieldManipulator TARGET_DELEGATING_MANIPULATOR = new IFieldManipulator() {

		@SuppressWarnings("unchecked")
		@Override
		public void setField(Object owner, Object target, Field field, Object value) {
			Preconditions.checkNotNull(target, "No value in field");
			((ISingleCustomProperty<Object>)target).set(owner, field, value);
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object getField(Object owner, Object target, Field field) {
			Preconditions.checkNotNull(target, "No value in field");
			return ((ISingleCustomProperty<Object>)target).get(owner, field);
		}
	};

	public static IFieldManipulator getProvider(Class<?> fieldType, boolean isDelegating) {
		if (isDelegating) return OWNER_DELEGATING_MANIPULATOR;
		if (ISingleCustomProperty.class.isAssignableFrom(fieldType)) return TARGET_DELEGATING_MANIPULATOR;
		return FIELD_MANIPULATOR;
	}

}
