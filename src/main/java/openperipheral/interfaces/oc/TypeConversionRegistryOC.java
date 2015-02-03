package openperipheral.interfaces.oc;

import java.util.Deque;

import li.cil.oc.api.machine.Value;
import openperipheral.api.converter.IGenericTypeConverter;
import openperipheral.converter.TypeConverter;

public class TypeConversionRegistryOC extends TypeConverter {

	public TypeConversionRegistryOC() {
		registerIgnored(Value.class, true);
	}

	@Override
	protected void addCustomConverters(Deque<IGenericTypeConverter> converters) {
		converters.add(new ConverterCallableOC());
	}

}
