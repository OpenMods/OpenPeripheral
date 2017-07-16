package openperipheral.adapter.types.classifier;

import net.minecraft.item.ItemStack;
import openperipheral.adapter.types.NamedTupleType;
import openperipheral.adapter.types.NamedTupleType.NamedTupleField;
import openperipheral.adapter.types.SingleType;
import openperipheral.api.adapter.IScriptType;
import openperipheral.api.adapter.ITypeClassifier;
import openperipheral.api.adapter.ITypeClassifier.IClassClassifier;

public class MinecraftTypeClassifier implements IClassClassifier {

	private static final IScriptType ITEM_STACK_TYPE;

	static {
		final NamedTupleField id = new NamedTupleField("id", SingleType.STRING, false);
		final NamedTupleField dmg = new NamedTupleField("dmg", SingleType.NUMBER, true);
		final NamedTupleField qty = new NamedTupleField("qty", SingleType.NUMBER, true);

		ITEM_STACK_TYPE = new NamedTupleType(id, dmg, qty, NamedTupleType.TAIL);
	}

	@Override
	public IScriptType classify(ITypeClassifier classifier, Class<?> cls) {
		if (cls == ItemStack.class) return ITEM_STACK_TYPE;
		return null;
	}
}
