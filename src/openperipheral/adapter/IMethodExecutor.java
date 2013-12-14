package openperipheral.adapter;

public interface IMethodExecutor {

	public MethodDeclaration getWrappedMethod();

	public boolean isSynthetic();
}
