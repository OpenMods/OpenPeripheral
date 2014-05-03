package openperipheral.adapter;

public interface IAdapterMethodsList<E extends IMethodExecutor> extends IMethodsHolder<E> {

	public Class<?> getTargetClass();

	public String describeType();

}