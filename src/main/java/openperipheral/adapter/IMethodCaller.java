package openperipheral.adapter;

import java.util.Set;

public interface IMethodCaller {
	public Set<Class<?>> requiredEnvArgs();

	public IMethodCall startCall(Object target);
}
