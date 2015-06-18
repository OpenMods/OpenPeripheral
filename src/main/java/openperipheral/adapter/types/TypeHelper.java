package openperipheral.adapter.types;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import openperipheral.adapter.method.TypeQualifier;
import openperipheral.api.adapter.method.ArgType;
import openperipheral.api.adapter.method.ReturnType;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class TypeHelper {

	public static IType single(ArgType type) {
		return SingleArgType.valueOf(type);
	}

	public static IType single(ReturnType type) {
		return SingleReturnType.valueOf(type);
	}

	public static IType create(ReturnType... types) {
		return createFromReturn(Arrays.asList(types));
	}

	public static IType create(ArgType... types) {
		return createFromArg(Arrays.asList(types));
	}

	public static IType single(ReturnType type, IRange range) {
		Preconditions.checkNotNull(type);
		Preconditions.checkNotNull(range);
		return new BoundedType(SingleReturnType.valueOf(type), range);
	}

	public static IType single(ArgType type, IRange range) {
		Preconditions.checkNotNull(type);
		Preconditions.checkNotNull(range);
		return new BoundedType(SingleArgType.valueOf(type), range);
	}

	public static IType bounded(IType type, IRange range) {
		Preconditions.checkNotNull(type);
		Preconditions.checkNotNull(range);
		return new BoundedType(type, range);
	}

	public static IType createFromReturn(List<ReturnType> types) {
		if (types.size() == 0) {
			return IType.VOID;
		} else if (types.size() == 1) {
			return SingleReturnType.valueOf(types.get(0));
		} else {
			return wrapSingleReturnTypes(types);
		}
	}

	public static IType createFromArg(List<ArgType> types) {
		if (types.size() == 0) {
			return IType.VOID;
		} else if (types.size() == 1) {
			return SingleArgType.valueOf(types.get(0));
		} else {
			return wrapSingleArgTypes(types);
		}
	}

	public static IType wrapSingleTypes(ReturnType... types) {
		return wrapSingleReturnTypes(Arrays.asList(types));
	}

	public static IType wrapSingleTypes(ArgType... types) {
		return wrapSingleArgTypes(Arrays.asList(types));
	}

	public static IType wrapSingleReturnTypes(List<ReturnType> types) {
		List<IType> result = Lists.newArrayList();

		for (ReturnType t : types)
			result.add(SingleReturnType.valueOf(t));

		return new TupleType(result);
	}

	public static IType wrapSingleArgTypes(List<ArgType> types) {
		List<IType> result = Lists.newArrayList();

		for (ArgType t : types)
			result.add(SingleArgType.valueOf(t));

		return new TupleType(result);
	}

	public static boolean isVoid(IType type) {
		return type == IType.VOID;
	}

	public static boolean compareTypes(IType left, IType right) {
		if (left == right) return true;
		if (left == null || right == null) return false;

		final String rightDescription = right.describe();
		final String leftDescription = left.describe();
		return leftDescription.equals(rightDescription);
	}

	public static IType interpretArgType(ArgType givenType, Type targetType) {
		return givenType == ArgType.AUTO? TypeQualifier.instance.qualifyType(targetType) : SingleArgType.valueOf(givenType);
	}

}
