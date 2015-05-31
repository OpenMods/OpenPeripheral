package openperipheral.adapter.types;

import java.util.Arrays;
import java.util.List;

import openperipheral.api.adapter.method.ArgType;
import openperipheral.api.adapter.method.ReturnType;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class TypeHelper {

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

	public static IType single(ArgType type) {
		Preconditions.checkNotNull(type);
		return new SingleArgType(type);
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

}
