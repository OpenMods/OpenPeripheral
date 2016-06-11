package openperipheral.adapter.property;

import java.lang.reflect.Type;
import openperipheral.api.adapter.IScriptType;

public abstract class IndexedTypeInfo {
	public final Type keyType;

	public final IScriptType keyDocType;

	public final IScriptType valueDocType;

	public IndexedTypeInfo(Type keyType, IScriptType keyDocType, IScriptType valueDocType) {
		this.keyType = keyType;
		this.keyDocType = keyDocType;
		this.valueDocType = valueDocType;
	}

	public abstract Type getValueType(Object target, Object key);
}