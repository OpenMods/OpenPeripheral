package openperipheral.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import openperipheral.api.ITypeConverter;
import dan200.computer.api.ILuaObject;

public class TypeConversionRegistry {

	private static ArrayList<ITypeConverter> converters = new ArrayList<ITypeConverter>();

	public static void registerTypeConverter(ITypeConverter converter) {
		converters.add(converter);
	}

	public static Object fromLua(Object obj, Class<?> type) {
		for (ITypeConverter converter : converters) {
			Object response = converter.fromLua(obj, type);
			if (response != null) { return response; }
		}
		return obj;
	}

	public static Object toLua(Object obj) {
		if (obj == null) {
			return null;
		} else if (obj instanceof ILuaObject) { return obj; }
		for (ITypeConverter converter : converters) {
			Object response = converter.toLua(obj);
			if (response != null) { return response; }
		}
		if (obj instanceof Map) { return obj; }
		if (obj.getClass().isPrimitive() || isWrapperType(obj.getClass())) {
			return obj;
		}
		return obj.toString();
	}
	private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    public static boolean isWrapperType(Class<?> clazz)
    {
        return WRAPPER_TYPES.contains(clazz);
    }

    private static Set<Class<?>> getWrapperTypes()
    {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        return ret;
    }

}
