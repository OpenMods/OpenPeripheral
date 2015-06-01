package openperipheral.adapter.property;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import openperipheral.api.adapter.IIndexedPropertyCallback;
import openperipheral.api.adapter.IPropertyCallback;
import openperipheral.api.helpers.Index;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

public class FieldManipulatorProviders {

	@SuppressWarnings("unchecked")
	public static <T> T getContents(Object target, Field field) {
		try {
			final T container = (T)field.get(target);
			Preconditions.checkNotNull(container, "Can't index nil value");
			return container;
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

	public static int getIndex(Object index) {
		Preconditions.checkArgument(index instanceof Index, "Invalid index type, expecting number");
		return ((Index)index).unbox();
	}

	public static final IFieldManipulator DEFAULT = new IFieldManipulator() {
		@Override
		public void setField(Object owner, Field field, Object value) {
			setContents(owner, field, value);
		}

		@Override
		public Object getField(Object owner, Field field) {
			return getContents(owner, field);
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

	private static class ArrayFieldManipulator implements IIndexedFieldManipulator {

		@Override
		public void setField(Object target, Field field, Object index, Object value) {
			final Object container = getContents(target, field);
			final int i = getIndex(index);
			try {
				Array.set(container, i, value);
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new IllegalArgumentException("Failed to set value at index " + index);
			}
		}

		@Override
		public Object getField(Object target, Field field, Object index) {
			final Object container = getContents(target, field);
			final int i = getIndex(index);

			try {
				return Array.get(container, i);
			} catch (ArrayIndexOutOfBoundsException e) {
				return null;
			}
		}
	}

	public static final IIndexedFieldManipulator ARRAY_MANIPULATOR = new ArrayFieldManipulator();

	private static class ExpandingArrayFieldManipulator extends ArrayFieldManipulator {
		@Override
		public void setField(Object target, Field field, Object index, Object value) {
			Object container = getContents(target, field);

			final int i = getIndex(index);
			if (i < 0) throw new IllegalArgumentException("Failed to set value at index " + i);

			final int length = Array.getLength(container);
			if (i >= length) {
				Class<?> componentCls = container.getClass().getComponentType();
				final int newLength = i + 1;
				Object newArray = Array.newInstance(componentCls, newLength);
				System.arraycopy(container, 0, newArray, 0, length);
				container = newArray;
				setContents(target, field, container);
			}

			Array.set(container, i, value);
		}
	}

	public static final IIndexedFieldManipulator ARRAY_EXPANDING_MANIPULATOR = new ExpandingArrayFieldManipulator();

	private static class ListFieldManipulator implements IIndexedFieldManipulator {
		@Override
		public void setField(Object target, Field field, Object index, Object value) {
			final List<Object> container = getContents(target, field);
			final int i = getIndex(index);

			try {
				container.set(i, value);
			} catch (IndexOutOfBoundsException e) {
				throw new IllegalArgumentException("Failed to set value at index " + index);
			}
		}

		@Override
		public Object getField(Object target, Field field, Object index) {
			final List<Object> container = getContents(target, field);
			final int i = getIndex(index);

			try {
				return container.get(i);
			} catch (IndexOutOfBoundsException e) {
				return null;
			}
		}
	}

	public static final IIndexedFieldManipulator LIST_MANIPULATOR = new ListFieldManipulator();

	private static class ExpandingListFieldManipulator extends ListFieldManipulator {

		@Override
		public void setField(Object target, Field field, Object index, Object value) {
			final List<Object> container = getContents(target, field);
			final int i = getIndex(index);

			if (i < 0) throw new IllegalArgumentException("Failed to set value at index " + i);

			while (i >= container.size())
				container.add(null);

			container.set(i, value);
		}
	}

	public static final IIndexedFieldManipulator EXPANDING_LIST_MANIPULATOR = new ExpandingListFieldManipulator();

	private static class MapFieldManipulator implements IIndexedFieldManipulator {
		@Override
		public void setField(Object target, Field field, Object index, Object value) {
			final Map<Object, Object> container = getContents(target, field);
			Preconditions.checkArgument(container.containsKey(index), "Invalid key = %s", index);
			container.put(index, value);

		}

		@Override
		public Object getField(Object target, Field field, Object index) {
			final Map<Object, Object> container = getContents(target, field);
			return container.get(index);
		}
	}

	public static final IIndexedFieldManipulator MAP_MANIPULATOR = new MapFieldManipulator();

	private static class ExpandingMapFieldManipulator extends MapFieldManipulator {
		@Override
		public void setField(Object target, Field field, Object index, Object value) {
			final Map<Object, Object> container = getContents(target, field);
			container.put(index, value);
		}
	}

	public static final IIndexedFieldManipulator INDEXED_DELEGATING = new IIndexedFieldManipulator() {
		@Override
		public void setField(Object target, Field field, Object index, Object value) {
			((IIndexedPropertyCallback)target).setField(field, index, value);
		}

		@Override
		public Object getField(Object target, Field field, Object index) {
			return ((IIndexedPropertyCallback)target).getField(field, index);
		}
	};

	public static final IIndexedFieldManipulator EXPANDING_MAP_MANIPULATOR = new ExpandingMapFieldManipulator();

	public static IIndexedFieldManipulator getIndexedProvider(Class<?> fieldType, boolean isDelegating, boolean isExpanding) {
		if (isDelegating) return INDEXED_DELEGATING;

		if (Map.class.isAssignableFrom(fieldType)) return isExpanding? EXPANDING_MAP_MANIPULATOR : MAP_MANIPULATOR;
		else if (List.class.isAssignableFrom(fieldType)) return isExpanding? EXPANDING_LIST_MANIPULATOR : LIST_MANIPULATOR;
		else if (fieldType.isArray()) return isExpanding? ARRAY_EXPANDING_MANIPULATOR : ARRAY_MANIPULATOR;

		throw new IllegalArgumentException("Failed to create manipulator for " + fieldType);
	}
}
