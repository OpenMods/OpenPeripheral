package openperipheral.interfaces.cc;

import openperipheral.converter.TypeConverter;
import dan200.computercraft.api.lua.ILuaObject;

public class TypeConversionRegistryCC extends TypeConverter {

	public TypeConversionRegistryCC() {
		registerIgnored(ILuaObject.class, true);
		outbound.addFirst(new ConverterCallableCC());
	}

}
