package openperipheral.adapter.types;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import openperipheral.adapter.method.TypeQualifier;
import openperipheral.api.adapter.method.ArgType;
import openperipheral.api.adapter.method.ReturnType;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

public class TypeHelper {

	public static final IType ARG_TABLE = TypeHelper.single(ArgType.TABLE);
	public static final IType ARG_NUMBER = TypeHelper.single(ArgType.NUMBER);
	public static final IType ARG_VOID = TypeHelper.single(ArgType.VOID);
	public static final IType ARG_BOOLEAN = TypeHelper.single(ArgType.BOOLEAN);
	public static final IType ARG_STRING = TypeHelper.single(ArgType.STRING);
	public static final IType ARG_OBJECT = TypeHelper.single(ArgType.OBJECT);

	public static IType create(ReturnType... types) {
		return createFromReturn(Arrays.asList(types));
	}

	public static IType create(ArgType... types) {
		return createFromArg(Arrays.asList(types));
	}

	public static IType single(ReturnType type) {
		Preconditions.checkNotNull(type);
		return new SingleReturnType(type);
	}

	public static IType single(ReturnType type, IRange range) {
		Preconditions.checkNotNull(type);
		Preconditions.checkNotNull(range);
		return new BoundedType(new SingleReturnType(type), range);
	}

	public static IType single(ArgType type) {
		Preconditions.checkNotNull(type);
		return new SingleArgType(type);
	}

	public static IType single(ArgType type, IRange range) {
		Preconditions.checkNotNull(type);
		Preconditions.checkNotNull(range);
		return new BoundedType(new SingleArgType(type), range);
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
			return new SingleReturnType(types.get(0));
		} else {
			return wrapSingleReturnTypes(types);
		}
	}

	public static IType createFromArg(List<ArgType> types) {
		if (types.size() == 0) {
			return IType.VOID;
		} else if (types.size() == 1) {
			return new SingleArgType(types.get(0));
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
			result.add(new SingleReturnType(t));

		return new TupleType(result);
	}

	public static IType wrapSingleArgTypes(List<ArgType> types) {
		List<IType> result = Lists.newArrayList();

		for (ArgType t : types)
			result.add(new SingleArgType(t));

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

	public static IType interpretArgType(ArgType givenType, Class<?> targetType) {
		return givenType == ArgType.AUTO? TypeQualifier.qualifyArgType(targetType) : TypeHelper.single(givenType);
	}

	public static IType interpretArgType(ArgType givenType, Type type) {
		final Class<?> rawType = TypeToken.of(type).getRawType();
		return interpretArgType(givenType, rawType);
	}

}
