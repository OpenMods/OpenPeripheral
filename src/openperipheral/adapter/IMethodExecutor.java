package openperipheral.adapter;

public interface IMethodExecutor {

	public IDescriptable getWrappedMethod();

	public boolean isSynthetic();
}
