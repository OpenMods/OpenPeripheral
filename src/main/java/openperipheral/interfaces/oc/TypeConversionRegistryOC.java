package openperipheral.interfaces.oc;

import li.cil.oc.api.machine.Value;
import openperipheral.converter.TypeConverter;

public class TypeConversionRegistryOC extends TypeConverter {

	private static final int LUA_OFFSET = 1;

	public TypeConversionRegistryOC() {
		super(LUA_OFFSET);
		registerIgnored(Value.class, true);
		outbound.addFirst(new ConverterCallableOC());
	}
}
