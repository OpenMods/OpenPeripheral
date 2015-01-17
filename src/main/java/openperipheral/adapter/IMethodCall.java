package openperipheral.adapter;

public interface IMethodCall {

	public IMethodCall setPositionalArg(int index, Object value);

	public IMethodCall setOptionalArg(String name, Object value);

	public Object[] call(Object[] args) throws Exception;

}
