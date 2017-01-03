package openperipheral.converter;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import javax.annotation.Nullable;
import openmods.reflection.TypeUtils;
import openmods.utils.CachedFactory;
import openperipheral.api.converter.IConverter;
import openperipheral.api.struct.ScriptStruct;
import openperipheral.api.struct.ScriptStruct.Output;
import openperipheral.api.struct.StructField;

public class StructHandlerProvider {

	public static class InvalidStructureException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public InvalidStructureException(Class<?> cls, Throwable cause) {
			super("Invalid structure: " + cls, cause);
		}

		public InvalidStructureException(String message) {
			super(message);
		}
	}

	public static final StructHandlerProvider instance = new StructHandlerProvider();

	public interface IFieldHandler {
		public int index();

		public String name();

		public Type type();

		public boolean isOptional();

		public Object get(Object target);

		public void set(Object target, Object value);
	}

	public interface IStructHandler {
		public Object toJava(IConverter converter, Map<?, ?> obj, int indexOffset);

		public Map<?, ?> fromJava(IConverter converter, Object obj, int indexOffset);

		public IFieldHandler field(String name);

		public List<IFieldHandler> fields();

		public ScriptStruct.Output defaultOutput();
	}

	private static final Ordering<Field> FIELD_NAME_ORDERING = Ordering.natural().onResultOf(new Function<Field, String>() {

		@Override
		public String apply(@Nullable Field input) {
			return input != null? input.getName() : "";
		}
	});

	private static class FieldHandler implements IFieldHandler {

		private final Type type;

		private final Field field;

		private final String name;

		private final int index;

		private final boolean isOptional;

		public FieldHandler(Class<?> ownerCls, Field field, String name, int index, boolean isOptional) {
			TypeToken<?> fieldType = TypeUtils.resolveFieldType(ownerCls, field);
			this.type = fieldType.getType();
			this.field = field;
			this.name = name;
			this.index = index;
			this.isOptional = isOptional;
		}

		@Override
		public Type type() {
			return type;
		}

		@Override
		public boolean isOptional() {
			return isOptional;
		}

		@Override
		public int index() {
			return index;
		}

		@Override
		public String name() {
			return name;
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

		private final List<IFieldHandler> indexedFields;

		private final Set<IFieldHandler> optionalFields;

		private final ScriptStruct.Output output;

		public StructHandler(ScriptStruct meta, Constructor<?> ctor) {
			this.ctor = ctor;
			this.output = meta.defaultOutput();

			ImmutableSet.Builder<IFieldHandler> optionalFields = ImmutableSet.builder();

			final Class<?> cls = this.ctor.getDeclaringClass();
			final List<Field> sortedFields = Lists.newArrayList(cls.getFields());
			Collections.sort(sortedFields, FIELD_NAME_ORDERING);

			final SortedMap<Integer, IFieldHandler> indexedFields = Maps.newTreeMap();

			int autoIndex = 0;
			for (Field field : sortedFields) {
				final StructField fieldMarker = field.getAnnotation(StructField.class);
				if (fieldMarker == null) continue;

				final boolean isOptional = fieldMarker.optional();

				final int markerIndex = fieldMarker.index();
				final int index = (markerIndex != StructField.AUTOASSIGN)? markerIndex : autoIndex;
				autoIndex++;

				FieldHandler handler = new FieldHandler(cls, field, field.getName(), index, isOptional);
				final IFieldHandler prev = indexedFields.put(index, handler);
				if (prev != null) throw new IllegalArgumentException(String.format("Duplicate index %d on fields %s and %s", index, handler.name(), prev.name()));

				if (isOptional) optionalFields.add(handler);
			}

			this.optionalFields = optionalFields.build();

			final int fieldCount = indexedFields.size();
			IFieldHandler[] collectedFields = new IFieldHandler[fieldCount];

			for (IFieldHandler handler : indexedFields.values()) {
				final int index = handler.index();
				Preconditions.checkArgument(index >= 0, "Negative index on field %s", handler.name());
				Preconditions.checkArgument(index < fieldCount, "Non-continuous field numbering on field %s (max index allowed: %s)", handler.name(), fieldCount - 1);
				collectedFields[index] = handler;
			}

			this.indexedFields = ImmutableList.copyOf(collectedFields);

			ImmutableMap.Builder<String, IFieldHandler> namedFields = ImmutableMap.builder();

			for (IFieldHandler handler : collectedFields) {
				Preconditions.checkArgument(handler != null, "Non-continuous field numbering");
				namedFields.put(handler.name(), handler);
			}

			this.namedFields = namedFields.build();
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
					final int index = ((Number)key).intValue() - indexOffset;

					Preconditions.checkArgument(index < indexedFields.size(), "Index %s is outside of allowed range for structure", index);
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
		public Map<?, ?> fromJava(IConverter converter, Object obj, final int indexOffset) {
			if (output == Output.OBJECT) {
				final Map<String, Object> result = Maps.newHashMap();
				for (Map.Entry<String, IFieldHandler> e : namedFields.entrySet())
					addFieldFromJava(converter, obj, result, e.getKey(), e.getValue());

				return result;
			} else {
				final Map<Integer, Object> result = Maps.newHashMap();
				int index = indexOffset;
				for (IFieldHandler handler : indexedFields)
					addFieldFromJava(converter, obj, result, index++, handler);
				return result;
			}

		}

		private static <T> void addFieldFromJava(IConverter converter, Object obj, Map<T, Object> result, T key, IFieldHandler f) {
			final Object value = f.get(obj);
			final Object converted = convertFromJava(converter, f, key, value);
			result.put(key, converted);
		}

		@Override
		public List<IFieldHandler> fields() {
			return indexedFields;
		}

		@Override
		public IFieldHandler field(String name) {
			return namedFields.get(name);
		}

		@Override
		public ScriptStruct.Output defaultOutput() {
			return output;
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
			if (cls.getEnclosingClass() != null && !Modifier.isStatic(cls.getModifiers()))
				throw new InvalidStructureException("Can't create serializer for not-static inner " + cls);

			final ScriptStruct struct = cls.getAnnotation(ScriptStruct.class);
			if (struct == null)
				throw new InvalidStructureException("Trying to generate serializer for unserializable " + cls);

			try {
				final Constructor<?> ctor = cls.getConstructor();
				return new StructHandler(struct, ctor);
			} catch (Exception e) {
				throw new InvalidStructureException(cls, e);
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
