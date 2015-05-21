package openperipheral.converter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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

	public interface IStructConverter {
		public Object toJava(IConverter converter, Map<?, ?> obj);

		public Map<?, ?> fromJava(IConverter converter, Object obj);
	}

	private static final IStructConverter DUMMY_CONVERTER = new IStructConverter() {
		@Override
		public Object toJava(IConverter converter, Map<?, ?> obj) {
			return null;
		}

		@Override
		public Map<?, ?> fromJava(IConverter converter, Object obj) {
			return ImmutableMap.of();
		}
	};

	private static final Ordering<Field> FIELD_ORDERING = Ordering.natural().onResultOf(new Function<Field, String>() {

		@Override
		public String apply(@Nullable Field input) {
			return input != null? input.getName() : "";
		}

	});

	private static class StructConverter implements IStructConverter {
		private final Constructor<?> ctor;

		private final Map<String, Field> namedFields;

		private final Map<Integer, Field> indexedFields;

		private final Set<Field> optionalFields;

		private final ScriptStruct.Output output;

		private final boolean tryIndexed;

		public StructConverter(ScriptStruct meta, Constructor<?> ctor) {
			this.ctor = ctor;
			this.output = meta.defaultOutput();
			this.tryIndexed = meta.allowTableInput();

			ImmutableMap.Builder<String, Field> namedFields = ImmutableMap.builder();
			ImmutableMap.Builder<Integer, Field> indexedFields = ImmutableMap.builder();
			ImmutableSet.Builder<Field> optionalFields = ImmutableSet.builder();

			List<Field> fields = Lists.newArrayList(this.ctor.getDeclaringClass().getFields());
			Collections.sort(fields, FIELD_ORDERING);

			int autoIndex = 0;
			for (Field f : fields) {
				final StructField fieldMarker = f.getAnnotation(StructField.class);

				if (fieldMarker == null) continue;

				namedFields.put(f.getName(), f);

				int index = fieldMarker.index();
				if (index == StructField.AUTOASSIGN) index = autoIndex;
				indexedFields.put(index + 1, f);

				if (fieldMarker.isOptional()) optionalFields.add(f);

				autoIndex++;
			}

			this.namedFields = namedFields.build();
			this.indexedFields = indexedFields.build();
			this.optionalFields = optionalFields.build();
		}

		@Override
		public Object toJava(IConverter converter, Map<?, ?> obj) {
			final Object result;
			try {
				result = ctor.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Failed to create object", e);
			}

			Set<Field> safeFields = Sets.newHashSet(optionalFields);

			for (Map.Entry<?, ?> e : obj.entrySet()) {
				Object key = e.getKey();
				Object value = e.getValue();
				if (key instanceof String) {
					final Field f = namedFields.get(key);
					Preconditions.checkArgument(f != null, "Extraneous field: %s = %s", key, value);

					setField(converter, result, key, f, value);
					safeFields.add(f);

				} else if (key instanceof Number) {
					Preconditions.checkArgument(tryIndexed, "Can't convert from table");
					final Field f = indexedFields.get(((Number)key).intValue());
					Preconditions.checkArgument(f != null, "Extraneous field: %s = %s", key, value);

					setField(converter, result, key, f, value);
					safeFields.add(f);
				} else {
					throw new IllegalArgumentException(String.format("Extraneous field %s = %s", key, value));
				}
			}

			for (Map.Entry<String, Field> e : namedFields.entrySet())
				if (!safeFields.contains(e.getValue())) throw new IllegalArgumentException(String.format("Field %s not set", e.getKey()));

			return result;
		}

		private static void setField(IConverter converter, Object obj, Object fieldKey, Field field, Object value) {
			try {
				final Object converted = converter.toJava(value, field.getGenericType());
				field.set(obj, converted);
			} catch (Exception ex) {
				throw new RuntimeException("Failed to set field " + fieldKey, ex);
			}
		}

		@Override
		public Map<?, ?> fromJava(IConverter converter, Object obj) {
			if (output == Output.OBJECT) {
				Map<String, Object> result = Maps.newHashMap();
				for (Map.Entry<String, Field> e : namedFields.entrySet())
					addFieldFromJava(converter, obj, result, e.getKey(), e.getValue());

				return result;
			} else {
				Map<Integer, Object> result = Maps.newHashMap();
				for (Map.Entry<Integer, Field> e : indexedFields.entrySet())
					addFieldFromJava(converter, obj, result, e.getKey(), e.getValue());
				return result;
			}

		}

		private static <T> void addFieldFromJava(IConverter converter, Object obj, Map<T, Object> result, T key, Field f) {
			try {
				final Object value = f.get(obj);
				final Object converted = converter.fromJava(value);
				result.put(key, converted);
			} catch (Exception ex) {
				throw new RuntimeException("Failed to convert value of field " + key, ex);
			}
		}
	}

	private final CachedFactory<Class<?>, IStructConverter> converters = new CachedFactory<Class<?>, IStructConverter>() {
		@Override
		protected IStructConverter create(Class<?> cls) {
			final ScriptStruct struct = cls.getAnnotation(ScriptStruct.class);
			if (struct == null) {
				Log.warn("Trying to generate serializer for unserializable %s", cls);
				return DUMMY_CONVERTER;
			}

			try {
				final Constructor<?> ctor = cls.getConstructor();
				return new StructConverter(struct, ctor);
			} catch (Exception e) {
				Log.warn(e, "Failed to find parameterless contstructor for class %s", cls);
				return DUMMY_CONVERTER;
			}
		}
	};

	public IStructConverter getConverter(Class<?> cls) {
		return converters.getOrCreate(cls);
	}

}
