package openperipheral.core;

import java.util.ArrayList;
import java.util.Map;

import openperipheral.api.ITypeConverter;
import dan200.computer.api.ILuaObject;

public class TypeConversionRegistry {

	private static ArrayList<ITypeConverter> converters = new ArrayList<ITypeConverter>();

	public static void registerTypeConverter(ITypeConverter converter) {
		converters.add(converter);
	}

	public static Object fromLua(Object obj, Class type) {
		for (ITypeConverter converter : converters) {
			Object response = converter.fromLua(obj, type);
			if (response != null) {
				return response;
			}
		}
		return obj;
	}

	public static Object toLua(Object obj) {
		if (obj == null) {
			return null;
		} else if (obj instanceof ILuaObject) {
			return obj;
		}
		for (ITypeConverter converter : converters) {
			Object response = converter.toLua(obj);
			if (response != null) {
				return response;
			}
		}
		if (obj instanceof Map) {
			return obj;
		}
		return obj.toString();
	}

}
