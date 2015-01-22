package openperipheral.converter;

import java.util.Map;

import openmods.Log;
import openperipheral.ApiProvider.IApiInstanceProvider;
import openperipheral.api.ITypeConvertersRegistry;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class TypeConvertersProvider implements IApiInstanceProvider<ITypeConvertersRegistry> {

	public static final TypeConvertersProvider INSTANCE = new TypeConvertersProvider();

	private final Map<String, ITypeConvertersRegistry> converters = Maps.newHashMap();

	private ITypeConvertersRegistry defaultConverter;

	public void registerConverter(String architecture, ITypeConvertersRegistry converter, boolean isDefault) {
		converters.put(architecture, converter);

		if (isDefault) defaultConverter = converter;
	}

	@Override
	public ITypeConvertersRegistry getInterface() {
		if (defaultConverter == null) {
			Preconditions.checkState(!converters.isEmpty(), "No type converter present");
			Map.Entry<String, ITypeConvertersRegistry> e = converters.entrySet().iterator().next();
			defaultConverter = e.getValue();
			Log.warn("No default type converter present, selecting '%s'", e.getKey());
		}

		return defaultConverter;
	}

	public ITypeConvertersRegistry getConverter(String architecture) {
		ITypeConvertersRegistry result = converters.get(architecture);
		Preconditions.checkNotNull(result, "Unknown architecture '%s'", architecture);
		return result;
	}

}
