package openperipheral.adapter.property;

import java.lang.reflect.Field;

import openperipheral.api.adapter.IPropertyCallback;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

public class CallbackProviders {

	public static final ICallbackProvider DEFAULT = new ICallbackProvider() {
		@Override
		public IPropertyCallback getCallback(final Object owner) {
			return new IPropertyCallback() {

				@Override
				public void setField(Field field, Object value) {
					try {
						field.set(owner, value);
					} catch (Throwable t) {
						throw Throwables.propagate(t);
					}
				}

				@Override
				public Object getField(Field field) {
					try {
						return field.get(owner);
					} catch (Throwable t) {
						throw Throwables.propagate(t);
					}
				}
			};
		}
	};

	public static final ICallbackProvider DELEGATING = new ICallbackProvider() {
		@Override
		public IPropertyCallback getCallback(Object target) {
			Preconditions.checkArgument(target instanceof IPropertyCallback, "Invalid target. Probably not your fault");
			return (IPropertyCallback)target;
		}
	};

	public static ICallbackProvider getProvider(boolean isDelegating) {
		return isDelegating? DELEGATING : DEFAULT;
	}

}
