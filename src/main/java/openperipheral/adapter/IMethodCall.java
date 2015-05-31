package openperipheral.adapter;

public interface IMethodCall {

	public IMethodCall setEnv(String name, Object value);

	public Object[] call(Object... args) throws Exception;

}
