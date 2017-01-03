package openperipheral.converter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.Map;
import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IConverterManager;
import openperipheral.api.converter.IGenericInboundTypeConverter;
import openperipheral.api.converter.IGenericTypeConverter;
import openperipheral.api.converter.IInboundTypeConverter;
import openperipheral.api.converter.IOutboundTypeConverter;
import openperipheral.api.converter.ITypeConverter;

public class TypeConvertersProvider implements IConverterManager {

	public static final TypeConvertersProvider INSTANCE = new TypeConvertersProvider();

	private final Map<String, IConverter> converters = Maps.newHashMap();

	public void registerConverter(String architecture, IConverter converter) {
		converters.put(architecture, converter);
	}

	@Override
	public void registerIgnored(Class<?> ignored, boolean includeSubclasses) {
		for (IConverter c : converters.values())
			c.registerIgnored(ignored, includeSubclasses);
	}

	@Override
	public void register(ITypeConverter converter) {
		for (IConverter c : converters.values())
			c.register(converter);
	}

	@Override
	public void register(IInboundTypeConverter converter) {
		for (IConverter c : converters.values())
			c.register(converter);
	}

	@Override
	public void register(IGenericInboundTypeConverter converter) {
		for (IConverter c : converters.values())
			c.register(converter);
	}

	@Override
	public void register(IOutboundTypeConverter converter) {
		for (IConverter c : converters.values())
			c.register(converter);
	}

	@Override
	public void register(IGenericTypeConverter converter) {
		for (IConverter c : converters.values())
			c.register(converter);
	}

	@Override
	public IConverter getConverter(String architecture) {
		IConverter result = converters.get(architecture);
		Preconditions.checkNotNull(result, "Unknown architecture '%s'", architecture);
		return result;
	}
}
