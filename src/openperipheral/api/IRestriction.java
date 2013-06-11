package openperipheral.api;

public interface IRestriction {
	public boolean isValid(Object value);

	public String getErrorMessage(int paramOffset);
}
