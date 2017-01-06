package openperipheral.adapter;

import com.google.common.base.Optional;
import java.util.Set;

public interface IMethodExecutor {
	public IMethodDescription description();

	public IMethodCall startCall(Object target);

	public boolean isAsynchronous();

	public Optional<String> getReturnSignal();

	public boolean canInclude(String architecture);

	public Set<Class<?>> requiredEnv();

	public Set<String> featureGroups();
}
