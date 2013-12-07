package openperipheral;

import java.util.List;
import java.util.Map;
import java.util.Set;

import openperipheral.api.ITypeConverter;
import openperipheral.converter.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import dan200.computer.api.ILuaObject;

public class TypeConversionRegistry {

	private static final List<ITypeConverter> CONVENTERS = Lists.newArrayList();

	static {
		CONVENTERS.add(new ConverterArray());
		CONVENTERS.add(new ConverterList());
		CONVENTERS.add(new ConverterDouble());
		CONVENTERS.add(new ConverterItemStack());
		CONVENTERS.add(new ConverterFluidTankInfo());
		CONVENTERS.add(new ConverterForgeDirection());
		CONVENTERS.add(new ConverterFluidTankInfo());
	}

	public static void registerTypeConverter(ITypeConverter converter) {
		CONVENTERS.add(converter);
	}

	private static final Set<Class<?>> WRAPPER_TYPES = Sets.newHashSet();

	static {
		WRAPPER_TYPES.add(Boolean.class);
		WRAPPER_TYPES.add(Character.class);
		WRAPPER_TYPES.add(Byte.class);
		WRAPPER_TYPES.add(Short.class);
		WRAPPER_TYPES.add(Integer.class);
		WRAPPER_TYPES.add(Long.class);
		WRAPPER_TYPES.add(Float.class);
		WRAPPER_TYPES.add(Double.class);
		WRAPPER_TYPES.add(Void.class);
	}

	public static boolean isWrapperType(Class<?> clazz)
	{
		return WRAPPER_TYPES.contains(clazz);
	}

	public static Object fromLua(Object obj, Class<?> type) {
		for (ITypeConverter converter : CONVENTERS) {
			Object response = converter.fromLua(obj, type);
			if (response != null) { return response; }
		}

		return obj;
	}

	public static Object toLua(Object obj) {
		if (obj == null || obj instanceof ILuaObject) return obj;

		for (ITypeConverter converter : CONVENTERS) {
			Object response = converter.toLua(obj);
			if (response != null) return response;
		}

		if (obj instanceof Map ||
				obj.getClass().isPrimitive() ||
				isWrapperType(obj.getClass())) return obj;

		return (obj instanceof Number)? ((Number)obj).doubleValue() : obj.toString();
	}

}
