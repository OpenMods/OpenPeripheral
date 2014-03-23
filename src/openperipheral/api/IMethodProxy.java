package openperipheral.api;

public interface IMethodProxy {
	public <T> T call(Object... args);
}
