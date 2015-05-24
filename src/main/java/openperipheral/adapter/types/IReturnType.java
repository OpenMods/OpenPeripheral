package openperipheral.adapter.types;

public interface IReturnType {
	public static IReturnType VOID = new IReturnType() {
		@Override
		public String describe() {
			return "()";
		}
	};

	public String describe();
}
