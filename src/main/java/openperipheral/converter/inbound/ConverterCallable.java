package openperipheral.converter.inbound;

import java.util.Map;

import openperipheral.api.adapter.method.ScriptObject;
import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IOutboundTypeConverter;

import com.google.common.collect.Maps;

public abstract class ConverterCallable implements IOutboundTypeConverter {

	private final Map<Class<?>, Boolean> cache = Maps.newHashMap();

	private synchronized boolean isCallable(Class<?> cls) {
		Boolean result = cache.get(cls);

		if (result == null) {
			result = cls.isAnnotationPresent(ScriptObject.class);
			cache.put(cls, result);
		}

		return result;
	}

	protected abstract Object wrap(Object o);

	@Override
	public Object fromJava(IConverter registry, Object obj) {
		if (isCallable(obj.getClass())) return wrap(obj);

		return null;
	}

}
