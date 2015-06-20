package openperipheral.adapter.types;

public class SetType implements IType {

	public final IType componentType;

	public SetType(IType componentType) {
		this.componentType = componentType;
	}

	@Override
	public String describe() {
		return "{" + componentType.describe() + "}";
	}

}
