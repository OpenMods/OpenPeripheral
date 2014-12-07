package openperipheral.api;

public interface IAdapterWithConstraints extends IAdapter {
	public boolean canApply(Class<?> target);
}
