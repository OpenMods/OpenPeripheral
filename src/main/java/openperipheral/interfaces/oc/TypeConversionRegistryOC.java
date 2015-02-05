package openperipheral.interfaces.oc;

import li.cil.oc.api.machine.Value;
import openperipheral.converter.TypeConverter;

public class TypeConversionRegistryOC extends TypeConverter {

	public TypeConversionRegistryOC() {
		registerIgnored(Value.class, true);
		outbound.addFirst(new ConverterCallableOC());
	}
}
