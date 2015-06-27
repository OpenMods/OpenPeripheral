package openperipheral.adapter.property;

import java.lang.reflect.Field;

import openmods.utils.SneakyThrower;

public class PropertyUtils {

	@SuppressWarnings("unchecked")
	public static <T> T getContents(Object target, Field field) {
		try {
			return (T)field.get(target);
		} catch (Throwable t) {
			throw SneakyThrower.sneakyThrow(t);
		}
	}

	public static void setContents(Object owner, Field field, Object value) {
		try {
			field.set(owner, value);
		} catch (Throwable t) {
			throw SneakyThrower.sneakyThrow(t);
		}
	}
}
