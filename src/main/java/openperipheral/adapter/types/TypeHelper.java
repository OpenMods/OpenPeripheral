package openperipheral.adapter.types;

import java.util.Arrays;
import java.util.List;

import openperipheral.api.adapter.method.ArgType;
import openperipheral.api.adapter.method.ReturnType;

import com.google.common.collect.Lists;

public class TypeHelper {

	public static IReturnType create(ReturnType... types) {
		return create(Arrays.asList(types));
	}

	public static IReturnType create(List<ReturnType> types) {
		if (types.size() == 0) {
			return IReturnType.VOID;
		} else if (types.size() == 1) {
			return new SingleReturnType(types.get(0));
		} else {
			return wrapSingleTypes(types);
		}
	}

	public static IReturnType wrapSingleTypes(ReturnType... types) {
		return wrapSingleTypes(Arrays.asList(types));
	}

	public static IReturnType wrapSingleTypes(List<ReturnType> types) {
		List<IReturnType> result = Lists.newArrayList();

		for (ReturnType t : types)
			result.add(new SingleReturnType(t));

		return new TupleReturnType(result);
	}

	public static boolean isVoid(IReturnType type) {
		return type == IReturnType.VOID;
	}

	public static ReturnType convert(ArgType type) {
		switch (type) {
			case BOOLEAN:
				return ReturnType.BOOLEAN;
			case NUMBER:
				return ReturnType.NUMBER;
			case STRING:
				return ReturnType.STRING;
			case TABLE:
				return ReturnType.TABLE;
			default:
				return ReturnType.OBJECT;
		}
	}
}
