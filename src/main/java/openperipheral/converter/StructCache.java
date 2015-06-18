package openperipheral.converter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

import javax.annotation.Nullable;

import openmods.Log;
import openmods.utils.CachedFactory;
import openperipheral.api.converter.IConverter;
import openperipheral.api.struct.ScriptStruct;
import openperipheral.api.struct.ScriptStruct.Output;
import openperipheral.api.struct.StructField;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;

public class StructCache {

	public static final StructCache instance = new StructCache();

	public interface IFieldHandler {
		public Type type();

		public Object get(Object target);

		public void set(Object target, Object value);
	}

	public interface IStructHandler {
		public Object toJava(IConverter converter, Map<?, ?> obj, int indexOffset);

		public Map<?, ?> fromJava(IConverter converter, Object obj, int indexOffset);

		public Set<String> fields();

		public IFieldHandler field(String name);
	}

	private static final IStructHandler DUMMY_CONVERTER = new IStructHandler() {
		@Override
		public Object toJava(IConverter converter, Map<?, ?> obj, int indexOffset) {
			return null;
		}

		@Override
		public Map<?, ?> fromJava(IConverter converter, Object obj, int indexOffset) {
			return ImmutableMap.of();
		}

		@Override
		public Set<String> fields() {
			return ImmutableSet.of();
		}

		@Override
		public IFieldHandler field(String field) {
			throw new UnsupportedOperationException();
		}
	};

	private static final Ordering<Field> FIELD_ORDERING = Ordering.natural().onResultOf(new Function<Field, String>() {

		@Override
		public String apply(@Nullable Field input) {
			return input != null? input.getName() : "";
		}
	});

	private static class FieldHandler implements IFieldHandler {

		private final Field field;

		public FieldHandler(Field field) {
			this.field = field;
		}

		@Override
		public Type type() {
			return field.getGenericType();
		}

		@Override
		public Object get(Object target) {
			try {
				return field.get(target);
			} catch (Exception ex) {
				throw new RuntimeException("Failed to get value of field " + field, ex);
			}
		}

		@Override
		public void set(Object target, Object value) {
			try {
				field.set(target, value);
			} catch (Exception ex) {
				throw new RuntimeException("Failed to set value of field " + field, ex);
			}
		}

	}

	private static class StructHandler implements IStructHandler {
		private final Constructor<?> ctor;

		private final Map<String, IFieldHandler> namedFields;

		private final Map<Integer, IFieldHandler> indexedFields;

		private final Set<IFieldHandler> optionalFields;

		private final ScriptStruct.Output output;

		private final boolean tryIndexed;

		public StructHandler(ScriptStruct meta, Constructor<?> ctor) {
			this.ctor = ctor;
			this.output = meta.defaultOutput();
			this.tryIndexed = meta.allowTableInput();

			ImmutableMap.Builder<String, IFieldHandler> namedFields = ImmutableMap.builder();
			ImmutableMap.Builder<Integer, IFieldHandler> indexedFields = ImmutableMap.builder();
			ImmutableSet.Builder<IFieldHandler> optionalFields = ImmutableSet.builder();

			List<Field> fields = Lists.newArrayList(this.ctor.getDeclaringClass().getFields());
			Collections.sort(fields, FIELD_ORDERING);

			int autoIndex = 0;
			for (Field f : fields) {
				final StructField fieldMarker = f.getAnnotation(StructField.class);

				if (fieldMarker == null) continue;

				FieldHandler handler = new FieldHandler(f);

				namedFields.put(f.getName(), handler);

				int index = fieldMarker.index();
				if (index == StructField.AUTOASSIGN) index = autoIndex;
				indexedFields.put(index, handler);

				if (fieldMarker.isOptional()) optionalFields.add(handler);

				autoIndex++;
			}

			this.namedFields = namedFields.build();
			this.indexedFields = indexedFields.build();
			this.optionalFields = optionalFields.build();
		}

		@Override
		public Object toJava(IConverter converter, Map<?, ?> obj, int indexOffset) {
			final Object result;
			try {
				result = ctor.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Failed to create object", e);
			}

			Set<IFieldHandler> safeFields = Sets.newIdentityHashSet();
			safeFields.addAll(optionalFields);

			for (Map.Entry<?, ?> e : obj.entrySet()) {
				Object key = e.getKey();
				Object value = e.getValue();
				if (key instanceof String) {
					final IFieldHandler f = namedFields.get(key);
					Preconditions.checkArgument(f != null, "Extraneous field: %s = %s", key, value);

					setField(converter, result, key, f, value);
					safeFields.add(f);

				} else if (key instanceof Number) {
					Preconditions.checkArgument(tryIndexed, "Can't convert from array");
					final int index = ((Number)key).intValue() - indexOffset;

					final IFieldHandler f = indexedFields.get(index);
					Preconditions.checkArgument(f != null, "Extraneous field: %s = %s", key, value);

					setField(converter, result, key, f, value);
					safeFields.add(f);
				} else {
					throw new IllegalArgumentException(String.format("Extraneous field %s = %s", key, value));
				}
			}

			for (Map.Entry<String, IFieldHandler> e : namedFields.entrySet())
				if (!safeFields.contains(e.getValue())) throw new IllegalArgumentException(String.format("Field %s not set", e.getKey()));

			return result;
		}

		private static void setField(IConverter converter, Object obj, Object fieldKey, IFieldHandler field, Object value) {
			final Object converted = convertToJava(converter, field, fieldKey, value);
			field.set(obj, converted);
		}

		private static Object convertToJava(IConverter converter, IFieldHandler field, Object key, Object value) {
			try {
				return converter.toJava(value, field.type());
			} catch (Exception ex) {
				throw new RuntimeException("Failed to convert field " + key, ex);
			}
		}

		private static Object convertFromJava(IConverter converter, IFieldHandler field, Object key, Object value) {
			try {
				return converter.fromJava(value);
			} catch (Exception ex) {
				throw new RuntimeException("Failed to convert field " + key, ex);
			}
		}

		@Override
		public Map<?, ?> fromJava(IConverter converter, Object obj, int indexOffset) {
			if (output == Output.OBJECT) {
				Map<String, Object> result = Maps.newHashMap();
				for (Map.Entry<String, IFieldHandler> e : namedFields.entrySet())
					addFieldFromJava(converter, obj, result, e.getKey(), e.getValue());

				return result;
			} else {
				Map<Integer, Object> result = Maps.newHashMap();
				for (Map.Entry<Integer, IFieldHandler> e : indexedFields.entrySet())
					addFieldFromJava(converter, obj, result, e.getKey() + indexOffset, e.getValue());
				return result;
			}

		}

		private static <T> void addFieldFromJava(IConverter converter, Object obj, Map<T, Object> result, T key, IFieldHandler f) {
			final Object value = f.get(obj);
			final Object converted = convertFromJava(converter, f, key, value);
			result.put(key, converted);
		}

		@Override
		public Set<String> fields() {
			return namedFields.keySet();
		}

		@Override
		public IFieldHandler field(String name) {
			return namedFields.get(name);
		}
	}

	private final CachedFactory<Class<?>, Boolean> checkedClasses = new CachedFactory<Class<?>, Boolean>() {
		@Override
		protected Boolean create(Class<?> key) {
			return key.getAnnotation(ScriptStruct.class) != null;
		}
	};

	private final CachedFactory<Class<?>, IStructHandler> handlers = new CachedFactory<Class<?>, IStructHandler>() {
		@Override
		protected IStructHandler create(Class<?> cls) {
			final ScriptStruct struct = cls.getAnnotation(ScriptStruct.class);
			if (struct == null) {
				Log.warn("Trying to generate serializer for unserializable %s", cls);
				return DUMMY_CONVERTER;
			}

			try {
				final Constructor<?> ctor = cls.getConstructor();
				return new StructHandler(struct, ctor);
			} catch (Exception e) {
				Log.warn(e, "Failed to find parameterless contstructor for class %s", cls);
				return DUMMY_CONVERTER;
			}
		}
	};

	public boolean isStruct(Class<?> cls) {
		return checkedClasses.getOrCreate(cls);
	}

	public IStructHandler getHandler(Class<?> cls) {
		return handlers.getOrCreate(cls);
	}

}
