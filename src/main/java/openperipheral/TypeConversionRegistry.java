package openperipheral;

import java.util.Deque;

import openmods.Log;
import openperipheral.api.ITypeConverter;
import openperipheral.api.ITypeConvertersRegistry;
import openperipheral.converter.*;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import dan200.computercraft.api.lua.ILuaObject;

@ApiSingleton
public class TypeConversionRegistry implements ITypeConvertersRegistry {

	public static final TypeConversionRegistry INSTANCE = new TypeConversionRegistry();

	private final Deque<ITypeConverter> converters = Lists.newLinkedList();

	private TypeConversionRegistry() {
		converters.add(new ConverterForgeDirection());
		converters.add(new ConverterFluidTankInfo());
		converters.add(new ConverterFluidTankInfo());
		converters.add(new ConverterItemStack());

		// DO NOT REORDER ANYTHING BELOW (unless you have good reason)
		converters.add(new ConverterArray());
		converters.add(new ConverterList());
		converters.add(new ConverterMap());
		converters.add(new ConverterSet());
		converters.add(new ConverterDefault());
		converters.add(new ConverterNumber());
		converters.add(new ConverterString());
	}

	@Override
	public void register(ITypeConverter converter) {
		converters.addFirst(converter);
	}

	@Override
	public Object fromLua(Object obj, Class<?> expected) {
		for (ITypeConverter converter : converters) {
			try {
				Object response = converter.fromLua(this, obj, expected);
				if (response != null) return response;
			} catch (Throwable e) {
				Log.warn(e, "Type converter %s failed", converter);
				throw Throwables.propagate(e);
			}
		}

		return null;
	}

	@Override
	public Object toLua(Object obj) {
		if (obj == null || obj instanceof ILuaObject) return obj;

		for (ITypeConverter converter : converters) {
			try {
				Object response = converter.toLua(this, obj);
				if (response != null) return response;
			} catch (Throwable e) {
				Log.warn(e, "Type converter %s failed", converter);
				throw Throwables.propagate(e);
			}
		}

		// should never get here, since ConverterString is catch-all
		throw new IllegalStateException("Conversion failed on value " + obj);
	}
}
