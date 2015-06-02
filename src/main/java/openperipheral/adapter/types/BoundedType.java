package openperipheral.adapter.types;

public class BoundedType implements IType {

	public final IType type;

	public final IRange range;

	public BoundedType(IType type, IRange range) {
		this.type = type;
		this.range = range;
	}

	@Override
	public String describe() {
		return type.describe() + range.describe();
	}

}
