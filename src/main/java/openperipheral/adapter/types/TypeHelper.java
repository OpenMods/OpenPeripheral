package openperipheral.adapter.types;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import openperipheral.adapter.TypeQualifier;
import openperipheral.api.adapter.IScriptType;
import openperipheral.api.adapter.method.ArgType;
import openperipheral.api.adapter.method.ReturnType;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class TypeHelper {

	public static IScriptType single(ArgType type) {
		return SingleArgType.valueOf(type);
	}

	public static IScriptType single(ReturnType type) {
		return SingleReturnType.valueOf(type);
	}

	public static IScriptType create(ReturnType... types) {
		return createFromReturn(Arrays.asList(types));
	}

	public static IScriptType create(ArgType... types) {
		return createFromArg(Arrays.asList(types));
	}

	public static IScriptType single(ReturnType type, IRange range) {
		Preconditions.checkNotNull(type);
		Preconditions.checkNotNull(range);
		return new BoundedType(SingleReturnType.valueOf(type), range);
	}

	public static IScriptType single(ArgType type, IRange range) {
		Preconditions.checkNotNull(type);
		Preconditions.checkNotNull(range);
		return new BoundedType(SingleArgType.valueOf(type), range);
	}

	public static IScriptType bounded(IScriptType type, IRange range) {
		Preconditions.checkNotNull(type);
		Preconditions.checkNotNull(range);
		return new BoundedType(type, range);
	}

	public static IScriptType createFromReturn(List<ReturnType> types) {
		if (types.size() == 0) {
			return SingleType.VOID;
		} else if (types.size() == 1) {
			return SingleReturnType.valueOf(types.get(0));
		} else {
			return wrapSingleReturnTypes(types);
		}
	}

	public static IScriptType createFromArg(List<ArgType> types) {
		if (types.size() == 0) {
			return SingleType.VOID;
		} else if (types.size() == 1) {
			return SingleArgType.valueOf(types.get(0));
		} else {
			return wrapSingleArgTypes(types);
		}
	}

	public static IScriptType wrapSingleTypes(ReturnType... types) {
		return wrapSingleReturnTypes(Arrays.asList(types));
	}

	public static IScriptType wrapSingleTypes(ArgType... types) {
		return wrapSingleArgTypes(Arrays.asList(types));
	}

	public static IScriptType wrapSingleReturnTypes(List<ReturnType> types) {
		List<IScriptType> result = Lists.newArrayList();

		for (ReturnType t : types)
			result.add(SingleReturnType.valueOf(t));

		return new TupleType(result);
	}

	public static IScriptType wrapSingleArgTypes(List<ArgType> types) {
		List<IScriptType> result = Lists.newArrayList();

		for (ArgType t : types)
			result.add(SingleArgType.valueOf(t));

		return new TupleType(result);
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

	public static IScriptType interpretArgType(ArgType givenType, Type targetType) {
		return givenType == ArgType.AUTO? TypeQualifier.INSTANCE.qualifyType(targetType) : SingleArgType.valueOf(givenType);
	}

}
