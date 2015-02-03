package openperipheral.interfaces.cc;

import java.util.Deque;

import openperipheral.api.converter.IGenericTypeConverter;
import openperipheral.converter.TypeConverter;
import dan200.computercraft.api.lua.ILuaObject;

public class TypeConversionRegistryCC extends TypeConverter {

	public TypeConversionRegistryCC() {
		registerIgnored(ILuaObject.class, true);
	}

	@Override
	protected void addCustomConverters(Deque<IGenericTypeConverter> converters) {
		converters.add(new ConverterCallableCC());
	}

}
