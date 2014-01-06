package openperipheral.util;

import java.lang.reflect.Field;
import java.util.Map;

import openperipheral.TypeConversionRegistry;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.text.WordUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import dan200.computer.api.ILuaContext;
import dan200.computer.api.ILuaObject;

public class LuaObjectBuilder {

	public interface IAccessCallback {
		public void setField(Field field, Object value);

		public Object getField(Field field);
	}

	public static ILuaObject build(Class<?> klazz, final IAccessCallback callback) {
		final Map<String, Field> fields = Maps.newHashMap();

		for (Field f : klazz.getFields()) {
			f.setAccessible(true);
			Property prop = f.getAnnotation(Property.class);
			if (prop == null) continue;

			String name = prop.name();
			if (Strings.isNullOrEmpty(name)) name = f.getName();

			fields.put(name.toLowerCase(), f);
		}

		final String[] methods = new String[2 * fields.size()];
		int index = 0;
		for (String name : fields.keySet()) {
			String capitalized = WordUtils.capitalizeFully(name);
			methods[index++] = "set" + capitalized;
			methods[index++] = "get" + capitalized;
		}

		return new ILuaObject() {

			@Override
			public String[] getMethodNames() {
				return methods;
			}

			@Override
			public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws Exception {
				String methodName = methods[method];
				String command = methodName.substring(0, 3);
				String fieldName = methodName.substring(3).toLowerCase();
				Field f = fields.get(fieldName);

				if ("set".equals(command)) {
					Preconditions.checkState(arguments.length == 1, "Exactly one parameter allowed");
					Object value = arguments[0];
					Object converted = TypeConversionRegistry.fromLua(value, f.getType());
					Preconditions.checkNotNull(value, "Invalid parameter type");
					callback.setField(f, converted);
					return ArrayUtils.EMPTY_OBJECT_ARRAY;
				} else if ("get".equals(command)) {
					Preconditions.checkState(arguments.length == 0, "No parameters allowed");
					Object value = callback.getField(f);
					Object converted = TypeConversionRegistry.toLua(value);
					return new Object[] { converted };
				}

				throw new RuntimeException("Something went wrong");
			}
		};
	}

	public static ILuaObject build(final Object target) {
		return build(target.getClass(), new IAccessCallback() {

			@Override
			public void setField(Field field, Object value) {
				try {
					field.set(target, value);
				} catch (Exception e) {
					throw Throwables.propagate(e);
				}
			}

			@Override
			public Object getField(Field field) {
				try {
					return field.get(target);
				} catch (Exception e) {
					throw Throwables.propagate(e);
				}
			}
		});
	}
}
