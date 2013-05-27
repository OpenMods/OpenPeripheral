package openperipheral;

import java.util.ArrayList;

public class TypeConversionRegistry {

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
		return null;
	}
	
	public static Object toLua(Object obj) {
		for (ITypeConverter converter : converters) {
			Object response = converter.toLua(obj);
			if (response != null) {
				return response;
			}
		}
		return null;
	}
}
