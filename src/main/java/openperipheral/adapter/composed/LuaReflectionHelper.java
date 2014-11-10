package openperipheral.adapter.composed;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import openperipheral.api.LuaCallable;
import openperipheral.api.LuaReturnType;
import openperipheral.api.Named;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class LuaReflectionHelper {
	private static Map<String, Object> describe(Method method) {
		Map<String, Object> desc = Maps.newHashMap();

		List<String> args = Lists.newArrayList();
		for (Class<?> arg : method.getParameterTypes())
			args.add(arg.toString());

		desc.put("modifiers", Modifier.toString(method.getModifiers()));
		desc.put("from", method.getDeclaringClass().toString());

		desc.put("args", args);
		return desc;
	}

	private static Map<String, Map<String, Object>> describe(Method[] methods) {
		Map<String, Map<String, Object>> results = Maps.newHashMap();
		for (Method method : methods)
			results.put(method.getName(), describe(method));

		return results;
	}

	@LuaCallable(returnTypes = LuaReturnType.STRING)
	public String getClass(@Named("target") Object owner) {
		return owner.getClass().toString();
	}

	@LuaCallable(returnTypes = LuaReturnType.STRING)
	public String getSuperclass(@Named("target") Object owner) {
		return owner.getClass().getSuperclass().toString();
	}

	@LuaCallable(returnTypes = LuaReturnType.TABLE)
	public List<String> getInterfaces(@Named("target") Object owner) {
		List<String> results = Lists.newArrayList();
		for (Class<?> cls : owner.getClass().getInterfaces())
			results.add(cls.toString());
		return results;
	}

	@LuaCallable(returnTypes = LuaReturnType.TABLE)
	public Map<String, Map<String, Object>> getMethods(@Named("target") Object owner) {
		return describe(owner.getClass().getMethods());
	}

	@LuaCallable(returnTypes = LuaReturnType.TABLE)
	public Map<String, Map<String, Object>> getDeclaredMethods(@Named("target") Object owner) {
		return describe(owner.getClass().getDeclaredMethods());
	}
}