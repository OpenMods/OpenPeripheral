package openperipheral.adapter;

import java.util.Map;

import com.google.common.base.Optional;

public interface IMethodExecutor {
	public IMethodDescription description();

	public IMethodCall startCall(Object target);

	public boolean isAsynchronous();

	public Optional<String> getReturnSignal();

	public boolean canInclude(String architecture);

	public Map<String, Class<?>> requiredEnv();
}
