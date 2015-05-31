package openperipheral.adapter.types;

public interface IType {
	public static IType VOID = new IType() {
		@Override
		public String describe() {
			return "()";
		}
	};

	public String describe();
}
