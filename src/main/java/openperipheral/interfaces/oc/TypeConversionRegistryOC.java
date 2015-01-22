package openperipheral.interfaces.oc;

import java.util.Deque;

import li.cil.oc.api.machine.Value;
import openperipheral.api.ITypeConverter;
import openperipheral.converter.TypeConversionRegistry;

public class TypeConversionRegistryOC extends TypeConversionRegistry {

	public TypeConversionRegistryOC() {
		registerIgnored(Value.class, true);
	}

	@Override
	protected void addCustomConverters(Deque<ITypeConverter> converters) {
		converters.add(new ConverterCallableOC());
	}

}
