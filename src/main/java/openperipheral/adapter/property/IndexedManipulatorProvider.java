package openperipheral.adapter.property;

import com.google.common.base.Preconditions;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import openperipheral.api.adapter.IIndexedPropertyCallback;
import openperipheral.api.helpers.Index;
import openperipheral.api.property.IIndexedCustomProperty;
import openperipheral.converter.StructHandlerProvider;
import openperipheral.converter.StructHandlerProvider.IFieldHandler;
import openperipheral.converter.StructHandlerProvider.IStructHandler;

public class IndexedManipulatorProvider {

	public static int getIndex(Object index) {
		Preconditions.checkArgument(index instanceof Index, "Invalid index type, got %s", index.getClass());
		return ((Index)index).value;
	}

	private abstract static class GenericFieldManipulator<T> implements IIndexedFieldManipulator {
		@Override
		@SuppressWarnings("unchecked")
		public final void setField(Object owner, Object target, Field field, Object index, Object value) {
			Preconditions.checkNotNull(target, "Can't index nil value");
			set(owner, (T)target, field, index, value);
		}

		public abstract void set(Object owner, T target, Field field, Object index, Object value);

		@Override
		@SuppressWarnings("unchecked")
		public final Object getField(Object owner, Object target, Field field, Object index) {
			Preconditions.checkNotNull(target, "Can't index nil value");
			return get(owner, (T)target, field, index);
		}

		public abstract Object get(Object owner, T target, Field field, Object index);
	}

	private abstract static class DefaultFieldManipulator extends GenericFieldManipulator<Object> {

	}

	private static class ArrayFieldManipulator extends DefaultFieldManipulator {

		@Override
		public void set(Object owner, Object target, Field field, Object index, Object value) {
			final int i = getIndex(index);
			try {
				Array.set(target, i, value);
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new IllegalArgumentException("Failed to set value at index " + index);
			}
		}

		@Override
		public Object get(Object owner, Object target, Field field, Object index) {
			final int i = getIndex(index);

			try {
				return Array.get(target, i);
			} catch (ArrayIndexOutOfBoundsException e) {
				return null;
			}
		}
	}

	public static final IIndexedFieldManipulator ARRAY_MANIPULATOR = new ArrayFieldManipulator();

	private static class ExpandingArrayFieldManipulator extends ArrayFieldManipulator {
		@Override
		public void set(Object owner, Object target, Field field, Object index, Object value) {
			final int i = getIndex(index);
			if (i < 0) throw new IllegalArgumentException("Negative index: " + i);

			final int length = Array.getLength(target);
			if (i >= length) {
				Class<?> componentCls = target.getClass().getComponentType();
				final int newLength = i + 1;
				Object newArray = Array.newInstance(componentCls, newLength);
				System.arraycopy(target, 0, newArray, 0, length);
				target = newArray;
				PropertyUtils.setContents(owner, field, target);
			}

			Array.set(target, i, value);
		}
	}

	public static final IIndexedFieldManipulator ARRAY_EXPANDING_MANIPULATOR = new ExpandingArrayFieldManipulator();

	private static class ListFieldManipulator extends GenericFieldManipulator<List<Object>> {
		@Override
		public void set(Object owner, List<Object> target, Field field, Object index, Object value) {
			final int i = getIndex(index);

			try {
				target.set(i, value);
			} catch (IndexOutOfBoundsException e) {
				throw new IllegalArgumentException("Failed to set value at index " + index);
			}
		}

		@Override
		public Object get(Object owner, List<Object> target, Field field, Object index) {
			final int i = getIndex(index);

			try {
				return target.get(i);
			} catch (IndexOutOfBoundsException e) {
				return null;
			}
		}
	}

	public static final IIndexedFieldManipulator LIST_MANIPULATOR = new ListFieldManipulator();

	private static class ExpandingListFieldManipulator extends ListFieldManipulator {

		@Override
		public void set(Object owner, List<Object> target, Field field, Object index, Object value) {
			final int i = getIndex(index);

			if (i < 0) throw new IllegalArgumentException("Negative index: " + i);

			while (i >= target.size())
				target.add(null);

			target.set(i, value);
		}
	}

	public static final IIndexedFieldManipulator LIST_EXPANDING_MANIPULATOR = new ExpandingListFieldManipulator();

	private static class MapFieldManipulator extends GenericFieldManipulator<Map<Object, Object>> {
		@Override
		public void set(Object owner, Map<Object, Object> target, Field field, Object index, Object value) {
			Preconditions.checkArgument(target.containsKey(index), "Can't add new key '%s' to map", index);
			target.put(index, value);

		}

		@Override
		public Object get(Object owner, Map<Object, Object> target, Field field, Object index) {
			return target.get(index);
		}
	}

	public static final IIndexedFieldManipulator MAP_MANIPULATOR = new MapFieldManipulator();

	private static class ExpandingMapFieldManipulator extends MapFieldManipulator {
		@Override
		public void set(Object owner, Map<Object, Object> target, Field field, Object index, Object value) {
			target.put(index, value);
		}
	}

	public static final IIndexedFieldManipulator MAP_EXPANDING_MANIPULATOR = new ExpandingMapFieldManipulator();

	public static final IIndexedFieldManipulator INDEXED_OWNER_DELEGATING_MANIPULATOR = new IIndexedFieldManipulator() {
		@Override
		public void setField(Object owner, Object target, Field field, Object index, Object value) {
			((IIndexedPropertyCallback)owner).setField(field, index, value);
		}

		@Override
		public Object getField(Object owner, Object target, Field field, Object index) {
			return ((IIndexedPropertyCallback)owner).getField(field, index);
		}
	};

	public static final IIndexedFieldManipulator INDEXED_TARGET_DELEGATING_MANIPULATOR = new GenericFieldManipulator<IIndexedCustomProperty<Object, Object>>() {

		@Override
		public void set(Object owner, IIndexedCustomProperty<Object, Object> target, Field field, Object index, Object value) {
			target.set(target, field, index, value);
		}

		@Override
		public Object get(Object owner, IIndexedCustomProperty<Object, Object> target, Field field, Object index) {
			return target.get(target, field, index);
		}
	};

	private static class StructFieldManipulator extends DefaultFieldManipulator {

		private final IStructHandler handler;

		public StructFieldManipulator(IStructHandler handler) {
			this.handler = handler;
		}

		private IFieldHandler getFieldHandler(Object index) {
			final String key = index.toString();
			final IFieldHandler fieldHandler = handler.field(key);
			Preconditions.checkNotNull(fieldHandler, "Invalid field name '%s'", index);
			return fieldHandler;
		}

		@Override
		public void set(Object owner, Object target, Field field, Object index, Object value) {
			final IFieldHandler fieldHandler = getFieldHandler(index);
			fieldHandler.set(target, value);
		}

		@Override
		public Object get(Object owner, Object target, Field field, Object index) {
			final IFieldHandler fieldHandler = getFieldHandler(index);
			return fieldHandler.get(target);
		}
	}

	public static IIndexedFieldManipulator createStructManipulator(Class<?> cls) {
		final IStructHandler handler = StructHandlerProvider.instance.getHandler(cls);
		return new StructFieldManipulator(handler);
	}

	public static IIndexedFieldManipulator getProvider(Class<?> fieldType, boolean isDelegating, boolean isExpanding) {
		if (isDelegating) return INDEXED_OWNER_DELEGATING_MANIPULATOR;
		if (IIndexedCustomProperty.class.isAssignableFrom(fieldType)) return INDEXED_TARGET_DELEGATING_MANIPULATOR;

		if (Map.class.isAssignableFrom(fieldType)) return isExpanding? MAP_EXPANDING_MANIPULATOR : MAP_MANIPULATOR;
		else if (List.class.isAssignableFrom(fieldType)) return isExpanding? LIST_EXPANDING_MANIPULATOR : LIST_MANIPULATOR;
		else if (fieldType.isArray()) return isExpanding? ARRAY_EXPANDING_MANIPULATOR : ARRAY_MANIPULATOR;
		else if (StructHandlerProvider.instance.isStruct(fieldType)) return createStructManipulator(fieldType);

		throw new IllegalArgumentException("Failed to create manipulator for " + fieldType);
	}
}
