package openperipheral.adapter.composed;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import openperipheral.api.adapter.Asynchronous;
import openperipheral.api.adapter.method.ScriptCallable;

@Asynchronous
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

	@ScriptCallable
	public String getClass(Object owner) {
		return owner.getClass().toString();
	}

	@ScriptCallable
	public String getSuperclass(Object owner) {
		return owner.getClass().getSuperclass().toString();
	}

	@ScriptCallable
	public List<String> getInterfaces(Object owner) {
		List<String> results = Lists.newArrayList();
		for (Class<?> cls : owner.getClass().getInterfaces())
			results.add(cls.toString());
		return results;
	}

	@ScriptCallable
	public Map<String, Map<String, Object>> getMethods(Object owner) {
		return describe(owner.getClass().getMethods());
	}

	@ScriptCallable
	public Map<String, Map<String, Object>> getDeclaredMethods(Object owner) {
		return describe(owner.getClass().getDeclaredMethods());
	}
}