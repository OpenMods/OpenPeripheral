package openperipheral.adapter.composed;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import openperipheral.adapter.IMethodDescription;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.api.adapter.Asynchronous;
import openperipheral.api.adapter.method.Arg;
import openperipheral.api.adapter.method.Optionals;
import openperipheral.api.adapter.method.ScriptCallable;
import openperipheral.util.DocUtils;

@Asynchronous
public class MethodsListerHelper {
	private final Map<String, IMethodExecutor> methods;
	private final Set<String> sources;

	public MethodsListerHelper(Map<String, IMethodExecutor> methods, Set<String> sources) {
		this.methods = methods;
		this.sources = sources;
	}

	@ScriptCallable(description = "List all the methods available")
	public String listMethods(@Optionals @Arg(name = "filterSource") String source) {
		List<String> info = Lists.newArrayList();
		for (Map.Entry<String, IMethodExecutor> e : methods.entrySet()) {
			final String name = e.getKey();
			final IMethodExecutor executor = e.getValue();

			final IMethodDescription desc = executor.description();
			if (source == null || source.equals(desc.source())) info.add(name + DocUtils.signature(desc));
		}
		Collections.sort(info);
		return Joiner.on(", ").join(info);
	}

	@ScriptCallable(description = "List all method sources")
	public Map<String, Boolean> listSources() {
		Map<String, Boolean> result = Maps.newTreeMap();
		for (String source : sources)
			result.put(source, Boolean.TRUE);
		return result;
	}

	@ScriptCallable(description = "Brief description of method")
	public String doc(@Arg(name = "method") String methodName) {
		IMethodExecutor method = methods.get(methodName);
		Preconditions.checkArgument(method != null, "Method not found");
		return DocUtils.doc(method.description());
	}

	@ScriptCallable(description = "Get a complete table of information about all available methods")
	public Map<?, ?> getAdvancedMethodsData(@Optionals @Arg(name = "method") String methodName) {
		if (methodName != null) {
			IMethodExecutor method = methods.get(methodName);
			Preconditions.checkArgument(method != null, "Method not found");
			return DocUtils.describe(method.description());
		} else {
			Map<String, Object> info = Maps.newHashMap();
			for (Map.Entry<String, IMethodExecutor> e : methods.entrySet()) {
				final IMethodDescription desc = e.getValue().description();
				info.put(e.getKey(), DocUtils.describe(desc));
			}
			return info;
		}
	}
}