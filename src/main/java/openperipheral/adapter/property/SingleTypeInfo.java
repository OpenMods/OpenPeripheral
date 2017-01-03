package openperipheral.adapter.property;

import java.lang.reflect.Type;
import openperipheral.api.adapter.IScriptType;

public abstract class SingleTypeInfo {

	public final IScriptType valueDocType;

	public SingleTypeInfo(IScriptType valueDocType) {
		this.valueDocType = valueDocType;
	}

	public abstract Type getValueType(Object target);
}