package openperipheral.api;

public interface IArchitectureAccess {

	public String architecture();

	public String peripheralName();

	public boolean signal(String name, Object... args);

	public Object wrapObject(Object target);
}
