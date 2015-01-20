package openperipheral.interfaces.cc;

import java.util.Deque;

import openperipheral.TypeConversionRegistry;
import openperipheral.api.ITypeConverter;
import dan200.computercraft.api.lua.ILuaObject;

public class TypeConversionRegistryCC extends TypeConversionRegistry {

	public TypeConversionRegistryCC() {
		registerIgnored(ILuaObject.class, true);
	}

	@Override
	protected void addCustomConverters(Deque<ITypeConverter> converters) {
		converters.add(new ConverterCallableCC());
	}

}
