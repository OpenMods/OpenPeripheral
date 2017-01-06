package openperipheral.adapter.method;

public class ArgumentDefinitionException extends IllegalStateException {
	private static final long serialVersionUID = -6428721405547878927L;

	public ArgumentDefinitionException(int argument, Throwable cause) {
		super(String.format("Failed to parse annotations on argument %d", argument), cause);
	}
}