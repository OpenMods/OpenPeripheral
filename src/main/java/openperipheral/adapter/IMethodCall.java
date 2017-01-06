package openperipheral.adapter;

public interface IMethodCall {

	public <T> IMethodCall setEnv(Class<? super T> intf, T instance);

	public Object[] call(Object... args) throws Exception;

}
