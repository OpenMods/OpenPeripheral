package openperipheral;

import java.util.Deque;
import java.util.Set;

import openperipheral.api.ITypeConverter;
import openperipheral.converter.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import dan200.computer.api.ILuaObject;

public class TypeConversionRegistry {

	private static final Deque<ITypeConverter> CONVENTERS = Lists.newLinkedList();

	static {
		CONVENTERS.add(new ConverterForgeDirection());
		CONVENTERS.add(new ConverterFluidTankInfo());
		CONVENTERS.add(new ConverterFluidTankInfo());
		CONVENTERS.add(new ConverterItemStack());

		// DO NOT REORDER ANYTHING BELOW (unless you have good reason)
		CONVENTERS.add(new ConverterArray());
		CONVENTERS.add(new ConverterList());
		CONVENTERS.add(new ConverterMap());
		CONVENTERS.add(new ConverterSet());
		CONVENTERS.add(new ConverterDefault());
		CONVENTERS.add(new ConverterNumber());
		CONVENTERS.add(new ConverterString());
	}

	public static void registerTypeConverter(ITypeConverter converter) {
		CONVENTERS.addFirst(converter);
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

	public static Object fromLua(Object obj, Class<?> expected) {
		for (ITypeConverter converter : CONVENTERS) {
			Object response = converter.fromLua(obj, expected);
			if (response != null) return response;
		}

		return null;
	}

	public static Object toLua(Object obj) {
		if (obj == null || obj instanceof ILuaObject) return obj;

		for (ITypeConverter converter : CONVENTERS) {
			Object response = converter.toLua(obj);
			if (response != null) return response;
		}

		// should never get here, since ConverterString is catch-all
		throw new IllegalStateException("Conversion failed on value " + obj);
	}
}
