package openperipheral.adapter.types;

public class MapType implements IType {

	public final IType keyType;

	public final IType valueType;

	public MapType(IType keyType, IType valueType) {
		this.keyType = keyType;
		this.valueType = valueType;
	}

	@Override
	public String describe() {
		return "{" + keyType.describe() + "->" + valueType.describe() + "}";
	}

}
