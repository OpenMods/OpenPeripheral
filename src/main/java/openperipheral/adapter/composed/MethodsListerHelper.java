package openperipheral.adapter.composed;

import java.util.List;
import java.util.Map;

import openperipheral.adapter.IDescriptable;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.api.LuaCallable;
import openperipheral.api.LuaReturnType;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MethodsListerHelper<E extends IMethodExecutor> {
	private final Map<String, E> methods;

	public MethodsListerHelper(Map<String, E> methods) {
		this.methods = methods;
	}

	@LuaCallable(returnTypes = LuaReturnType.STRING, description = "List all the methods available")
	public String listMethods() {
		List<String> info = Lists.newArrayList();
		for (Map.Entry<String, E> e : methods.entrySet()) {
			final IDescriptable m = e.getValue().getWrappedMethod();
			info.add(e.getKey() + m.signature());
		}
		return Joiner.on(", ").join(info);
	}

	@LuaCallable(returnTypes = LuaReturnType.TABLE, description = "Get a complete table of information about all available methods")
	public Map<?, ?> getAdvancedMethodsData() {
		Map<String, Object> info = Maps.newHashMap();
		for (Map.Entry<String, E> e : methods.entrySet()) {
			final IDescriptable m = e.getValue().getWrappedMethod();
			info.put(e.getKey(), m.describe());
		}
		return info;
	}
}