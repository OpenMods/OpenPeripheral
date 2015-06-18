package openperipheral.adapter.types;

public class SingleType implements IType {

	private final String type;

	public SingleType(String type) {
		this.type = type;
	}

	@Override
	public String describe() {
		return type;
	}

}
