package openperipheral.adapter.types;

import com.google.common.base.Preconditions;
import openperipheral.api.adapter.IScriptType;

public class TypeHelper {

	public static IScriptType bounded(IScriptType type, IRange range) {
		Preconditions.checkNotNull(type);
		Preconditions.checkNotNull(range);
		return new BoundedType(type, range);
	}

	public static boolean isVoid(IScriptType type) {
		return type == SingleType.VOID;
	}

	public static boolean compareTypes(IScriptType left, IScriptType right) {
		if (left == right) return true;
		if (left == null || right == null) return false;

		final String rightDescription = right.describe();
		final String leftDescription = left.describe();
		return leftDescription.equals(rightDescription);
	}

}
