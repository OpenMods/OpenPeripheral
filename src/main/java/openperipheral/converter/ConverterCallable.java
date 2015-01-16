package openperipheral.converter;

import java.util.Map;

import openperipheral.adapter.AdapterManager;
import openperipheral.api.ITypeConverter;
import openperipheral.api.ITypeConvertersRegistry;
import openperipheral.api.LuaObject;

import com.google.common.collect.Maps;

public class ConverterCallable implements ITypeConverter {

	private final Map<Class<?>, Boolean> cache = Maps.newHashMap();

	private synchronized boolean isCallable(Class<?> cls) {
		Boolean result = cache.get(cls);

		if (result == null) {
			result = cls.isAnnotationPresent(LuaObject.class);
			cache.put(cls, result);
		}

		return result;
	}

	@Override
	public Object fromLua(ITypeConvertersRegistry registry, Object obj, Class<?> expected) {
		return null;
	}

	@Override
	public Object toLua(ITypeConvertersRegistry registry, Object obj) {
		if (isCallable(obj.getClass())) return AdapterManager.wrapObject(obj);

		return null;
	}

}
