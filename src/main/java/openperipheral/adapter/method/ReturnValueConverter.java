package openperipheral.adapter.method;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import openmods.reflection.TypeUtils;
import openperipheral.adapter.types.SingleType;
import openperipheral.adapter.types.TupleReturnType;
import openperipheral.adapter.types.VarReturnType;
import openperipheral.adapter.types.classifier.TypeClassifier;
import openperipheral.api.adapter.IScriptType;
import openperipheral.api.adapter.method.IReturnTuple;
import openperipheral.api.converter.IConverter;

public abstract class ReturnValueConverter {

	private static abstract class VarReturn extends ReturnValueConverter {
		public VarReturn(TypeToken<?> componentType) {
			super(new VarReturnType(TypeClassifier.INSTANCE.classifyType(componentType.getType())));
		}
	}

	public static class ArrayVarReturn extends VarReturn {

		public ArrayVarReturn(TypeToken<?> componentType) {
			super(componentType);
		}

		@Override
		public Object[] convertReturns(IConverter converter, Object array) {
			final int length = Array.getLength(array);
			final Object[] result = new Object[length];

			for (int i = 0; i < length; i++)
				result[i] = converter.fromJava(Array.get(array, i));

			return result;
		}

	}

	public static class CollectionVarReturn extends VarReturn {

		public CollectionVarReturn(TypeToken<?> componentType) {
			super(componentType);
		}

		@Override
		public Object[] convertReturns(IConverter converter, Object result) {
			final Collection<?> resultCollection = (Collection<?>)result;
			final Object[] tmp = new Object[resultCollection.size()];
			int i = 0;
			for (Object o : resultCollection)
				tmp[i++] = converter.fromJava(o);

			return tmp;
		}

	}

	public static class SingleReturn extends ReturnValueConverter {

		public SingleReturn(TypeToken<?> type) {
			super(TypeClassifier.INSTANCE.classifyType(type.getType()));
		}

		@Override
		public Object[] convertReturns(IConverter converter, Object result) {
			return new Object[] { converter.fromJava(result) };
		}

	}

	public static class FixedReturn extends ReturnValueConverter {

		private final int size;

		public FixedReturn(List<IScriptType> types) {
			super(new TupleReturnType(types));
			this.size = types.size();
		}

		@Override
		public Object[] convertReturns(IConverter converter, Object result) {
			final IReturnTuple tuple = (IReturnTuple)result;
			final Object[] values = tuple.values();

			final Object[] convertedResult = new Object[size];

			for (int i = 0; i < Math.min(values.length, convertedResult.length); i++)
				convertedResult[i] = converter.fromJava(values[i]);

			return convertedResult;
		}

	}

	public static final TypeToken<?> TUPLE_TOKEN = TypeToken.of(IReturnTuple.class);

	private static final Object[] NO_RETURNS = new Object[0];

	private static final ReturnValueConverter EMPTY = new ReturnValueConverter(SingleType.VOID) {

		@Override
		public Object[] convertReturns(IConverter converter, Object result) {
			return NO_RETURNS;
		}

		@Override
		public boolean hasReturn() {
			return false;
		}
	};

	private final IScriptType type;

	private ReturnValueConverter(IScriptType type) {
		this.type = type;
	}

	public abstract Object[] convertReturns(IConverter converter, Object result);

	public IScriptType getDocType() {
		return type;
	}

	public boolean hasReturn() {
		return true;
	}

	public static ReturnValueConverter create(Type returnType, boolean varReturns) {
		final TypeToken<?> type = TypeToken.of(returnType);
		if (varReturns) {
			if (type.isArray()) {
				return new ArrayVarReturn(type.getComponentType());
			} else if (TypeUtils.COLLECTION_TOKEN.isAssignableFrom(type)) {
				return new CollectionVarReturn(type.resolveType(TypeUtils.COLLECTION_VALUE_PARAM));
			} else {
				throw new IllegalArgumentException("Methods with varadic returns must return array or collection");
			}
		} else if (TUPLE_TOKEN.isAssignableFrom(type)) {
			final List<IScriptType> types = classifyTuple(type);
			return new FixedReturn(types);
		} else if (returnType != Void.class && returnType != void.class) return new SingleReturn(type);

		return EMPTY;
	}

	private static List<IScriptType> classifyTuple(TypeToken<?> type) {
		final TypeVariable<?>[] typeParameters = type.getRawType().getTypeParameters();
		Preconditions.checkArgument(typeParameters.length > 1, "Type %s is not valid tuple return type - it has less than 2 type parameters");

		final IScriptType[] result = new IScriptType[typeParameters.length];
		for (int i = 0; i < typeParameters.length; i++) {
			final Type resolveParameter = type.resolveType(typeParameters[i]).getType();
			result[i] = TypeClassifier.INSTANCE.classifyType(resolveParameter);
		}

		return Arrays.asList(result);
	}
}
