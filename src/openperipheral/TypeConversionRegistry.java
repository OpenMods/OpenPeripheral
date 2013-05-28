package openperipheral;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TypeConversionRegistry {

	private static final Set<Class> WRAPPER_TYPES = new HashSet(Arrays.asList(
		    Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Void.class));
	
	private static ArrayList<ITypeConverter> converters = new ArrayList<ITypeConverter>();
	
	public static void registryTypeConverter(ITypeConverter converter) {
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
		}
		for (ITypeConverter converter : converters) {
			Object response = converter.toLua(obj);
			if (response != null) {
				return response;
			}
		}
		return obj.toString();
	}
	
}
