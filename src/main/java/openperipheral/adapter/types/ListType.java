package openperipheral.adapter.types;

public class ListType implements IType {

	public final IType componentType;

	public ListType(IType componentType) {
		this.componentType = componentType;
	}

	@Override
	public String describe() {
		return "[" + componentType.describe() + "]";
	}

}
