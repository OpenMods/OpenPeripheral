package openperipheral.converter;

import java.util.Map;

import openperipheral.ApiProvider.IApiInstanceProvider;
import openperipheral.ApiSingleton;
import openperipheral.api.ITypeConverter;
import openperipheral.api.ITypeConvertersProvider;
import openperipheral.api.ITypeConvertersRegistry;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

@ApiSingleton
public class TypeConvertersProvider implements IApiInstanceProvider<ITypeConvertersRegistry>, ITypeConvertersProvider {

	public static final TypeConvertersProvider INSTANCE = new TypeConvertersProvider();

	private final Map<String, ITypeConvertersRegistry> converters = Maps.newHashMap();

	private final ITypeConvertersRegistry common = new ITypeConvertersRegistry() {
		@Override
		public Object toLua(Object obj) {
			throw new UnsupportedOperationException("use 'converter' env argument");
		}

		@Override
		public Object fromLua(Object obj, Class<?> expected) {
			throw new UnsupportedOperationException("use 'converter' env argument");
		}

		@Override
		public void registerIgnored(Class<?> ignored, boolean includeSubclasses) {
			for (ITypeConvertersRegistry c : converters.values())
				c.registerIgnored(ignored, includeSubclasses);
		}

		@Override
		public void register(ITypeConverter converter) {
			for (ITypeConvertersRegistry c : converters.values())
				c.register(converter);
		}
	};

	public void registerConverter(String architecture, ITypeConvertersRegistry converter) {
		converters.put(architecture, converter);
	}

	@Override
	public ITypeConvertersRegistry getInterface() {
		return common;
	}

	@Override
	public ITypeConvertersRegistry getConverter(String architecture) {
		ITypeConvertersRegistry result = converters.get(architecture);
		Preconditions.checkNotNull(result, "Unknown architecture '%s'", architecture);
		return result;
	}
}
