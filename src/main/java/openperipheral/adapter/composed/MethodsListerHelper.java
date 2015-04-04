package openperipheral.adapter.composed;

import java.util.*;

import openperipheral.adapter.IDescriptable;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.api.adapter.Asynchronous;
import openperipheral.api.adapter.method.*;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Asynchronous
public class MethodsListerHelper {
	private final Map<String, IMethodExecutor> methods;
	private final Set<String> sources;

	public MethodsListerHelper(Map<String, IMethodExecutor> methods, Set<String> sources) {
		this.methods = methods;
		this.sources = sources;
	}

	@ScriptCallable(returnTypes = ReturnType.STRING, description = "List all the methods available")
	public String listMethods(@Optionals @Arg(name = "filterSource") String source) {
		List<String> info = Lists.newArrayList();
		for (Map.Entry<String, IMethodExecutor> e : methods.entrySet()) {
			final String name = e.getKey();
			final IMethodExecutor executor = e.getValue();

			final IDescriptable m = executor.description();
			if (source == null || source.equals(m.source())) info.add(name + m.signature());
		}
		Collections.sort(info);
		return Joiner.on(", ").join(info);
	}

	@ScriptCallable(returnTypes = ReturnType.TABLE, description = "List all method sources")
	public Map<String, Boolean> listSources() {
		Map<String, Boolean> result = Maps.newTreeMap();
		for (String source : sources)
			result.put(source, Boolean.TRUE);
		return result;
	}

	@ScriptCallable(returnTypes = ReturnType.STRING, description = "Brief description of method")
	public String doc(@Arg(name = "method") String methodName) {
		IMethodExecutor method = methods.get(methodName);
		Preconditions.checkArgument(method != null, "Method not found");
		return method.description().doc();
	}

	@ScriptCallable(returnTypes = ReturnType.TABLE, description = "Get a complete table of information about all available methods")
	public Map<?, ?> getAdvancedMethodsData(@Optionals @Arg(name = "method") String methodName) {
		if (methodName != null) {
			IMethodExecutor method = methods.get(methodName);
			Preconditions.checkArgument(method != null, "Method not found");
			return method.description().describe();
		} else {
			Map<String, Object> info = Maps.newHashMap();
			for (Map.Entry<String, IMethodExecutor> e : methods.entrySet()) {
				final IDescriptable m = e.getValue().description();
				info.put(e.getKey(), m.describe());
			}
			return info;
		}
	}
}