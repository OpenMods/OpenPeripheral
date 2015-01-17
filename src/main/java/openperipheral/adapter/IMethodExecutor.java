package openperipheral.adapter;

import java.util.Map;

public interface IMethodExecutor {
	public IDescriptable description();

	public IMethodCall startCall(Object target);

	public void validateArgs(Map<String, Class<?>> args);

	public boolean isAsynchronous();
}
