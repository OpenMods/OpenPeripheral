package openperipheral.adapter.types;

public interface IType {
	public static IType VOID = new SingleType("()");

	public static IType WILDCHAR = new SingleType("*");

	public static IType UNKNOWN = new SingleType("?");

	public String describe();
}
