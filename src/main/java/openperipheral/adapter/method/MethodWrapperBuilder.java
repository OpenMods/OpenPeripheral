package openperipheral.adapter.method;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import openmods.reflection.TypeUtils;
import openperipheral.adapter.AdapterLogicException;
import openperipheral.adapter.IMethodCall;
import openperipheral.adapter.IMethodCaller;
import openperipheral.adapter.IMethodDescription;
import openperipheral.adapter.types.TypeHelper;
import openperipheral.api.adapter.IScriptType;
import openperipheral.api.adapter.method.Alias;
import openperipheral.api.adapter.method.IMultiReturn;
import openperipheral.api.adapter.method.MultipleReturn;
import openperipheral.api.adapter.method.ReturnType;
import openperipheral.api.adapter.method.ScriptCallable;
import openperipheral.api.converter.IConverter;

public class MethodWrapperBuilder {

	private static final Object[] NO_RETURNS = new Object[0];

	private final List<String> names;
	private final String source;
	private final Method method;
	private final String description;
	private final List<ReturnType> returnTypes;
	private final IScriptType wrappedReturn;

	private final boolean validateReturn;

	private final boolean multipleReturn;

	private final boolean voidReturn;

	private final boolean requiresConverter;

	private final Map<Integer, Class<?>> unnamedEnvArg = Maps.newHashMap();

	private final Map<Class<?>, Integer> envArgsIndices = Maps.newHashMap();

	private static class IndexTypePair {
		private final int index;
		private final Class<?> type;

		public IndexTypePair(int index, Class<?> type) {
			this.index = index;
			this.type = type;
		}
	}

	private Optional<IndexTypePair> targetArg = Optional.absent();

	private final List<Argument> convertedArgs = Lists.newArrayList();

	private final int argCount;

	private static List<String> getNames(Method method, ScriptCallable meta) {
		ImmutableList.Builder<String> names = ImmutableList.builder();

		String mainName = meta.name();

		if (ScriptCallable.USE_METHOD_NAME.equals(mainName)) names.add(method.getName());
		else names.add(mainName);

		Alias alias = method.getAnnotation(Alias.class);
		if (alias != null) names.add(alias.value());
		return names.build();
	}

	public MethodWrapperBuilder(Class<?> rootClass, Method method, ScriptCallable meta, String source) {
		this.method = method;
		this.source = source;

		this.names = getNames(method, meta);

		this.description = meta.description();
		this.returnTypes = ImmutableList.copyOf(meta.returnTypes());
		this.validateReturn = meta.validateReturn();

		this.multipleReturn = method.isAnnotationPresent(MultipleReturn.class);
		this.voidReturn = method.getReturnType() == void.class;
		Preconditions.checkState(!(this.multipleReturn && this.voidReturn), "Conflict: multiple return methods cannot return void");

		this.wrappedReturn = TypeHelper.createFromReturn(returnTypes);

		if (validateReturn) validateResultCount();

		final List<ArgWrapper> args = ArgWrapper.fromMethod(rootClass, method);

		new ArgVisitor() {
			@Override
			protected void visitUnnamedArg(int argIndex, TypeToken<?> type) {
				unnamedEnvArg.put(argIndex, type.getRawType());
			}

			@Override
			protected void visitEnvArg(int argIndex, TypeToken<?> type) {
				final Class<?> rawType = type.getRawType();
				Integer prev = envArgsIndices.put(rawType, argIndex);
				if (prev != null) throw new IllegalStateException(String.format("Conflict on type %s, args: %s, %s", rawType, prev, argIndex));
			}

			@Override
			protected void visitScriptArg(int argIndex, Argument arg) {
				convertedArgs.add(arg);
			}

		}.visitArgs(args, method.isVarArgs());

		this.requiresConverter = !convertedArgs.isEmpty() || !this.voidReturn;
		this.argCount = unnamedEnvArg.size() + envArgsIndices.size() + convertedArgs.size();
		Preconditions.checkState(this.argCount == args.size(), "Internal error for method %s", method);
	}

	private void validateResultCount() {
		Class<?> javaReturn = method.getReturnType();

		final int returnLength = returnTypes.size();

		for (ReturnType t : returnTypes) {
			Preconditions.checkArgument(t != ReturnType.VOID, "Method '%s' declares Void as return type. Use empty list instead.", method);
		}

		if (javaReturn == void.class) {
			Preconditions.checkArgument(returnLength == 0, "Method '%s' returns nothing, but declares at least one Lua result", method);
		}

		if (returnLength == 0) {
			Preconditions.checkArgument(javaReturn == void.class, "Method '%s' returns '%s', but declares no Lua results", method, javaReturn);
		}

		if (multipleReturn) {
			Preconditions.checkArgument(IMultiReturn.class.isAssignableFrom(javaReturn) || Collection.class.isAssignableFrom(javaReturn) || javaReturn.isArray(), "Method '%s' declared more than one Lua result, but returns single '%s' instead of array, collection or IMultiReturn", method, javaReturn);
		}

		if (returnLength > 1) {
			Preconditions.checkArgument(IMultiReturn.class.isAssignableFrom(javaReturn) || multipleReturn, "Method '%s' declared more than one Lua result, but returns single '%s' instead of array, collection or IMultiReturn", method, javaReturn);
		}
	}

	private static void checkReturnType(int argIndex, ReturnType expected, Object actual) {
		final Class<?> expectedJava = expected.getJavaType();
		Preconditions.checkArgument(actual == null || expectedJava.isInstance(actual) || TypeUtils.compareTypes(expectedJava, actual.getClass()), "Invalid type of return value %s: expected %s, got '%s'", argIndex, expected, actual);
	}

	private void validateResult(Object... result) {
		if (returnTypes.isEmpty()) {
			Preconditions.checkArgument(result.length == 1 && result[0] == null, "Returning value from null method");
		} else {
			Preconditions.checkArgument(result.length == returnTypes.size(), "Returning invalid number of values from method %s, expected %s, got %s", method, returnTypes.size(), result.length);
			for (int i = 0; i < result.length; i++)
				checkReturnType(i, returnTypes.get(i), result[i]);
		}
	}

	private static Object[] convertMultiResult(IConverter converter, IMultiReturn result) {
		return convertVarResult(converter, result.getObjects());
	}

	private static Object[] convertCollectionResult(IConverter converter, Collection<?> result) {
		Object[] tmp = new Object[result.size()];
		int i = 0;
		for (Object o : result)
			tmp[i++] = converter.fromJava(o);

		return tmp;
	}

	private static Object[] convertArrayResult(IConverter converter, Object array) {
		int length = Array.getLength(array);
		Object[] result = new Object[length];

		for (int i = 0; i < length; i++)
			result[i] = converter.fromJava(Array.get(array, i));

		return result;
	}

	private static Object[] convertVarResult(IConverter converter, Object... result) {
		for (int i = 0; i < result.length; i++)
			result[i] = converter.fromJava(result[i]);

		return result;
	}

	private Object[] convertResult(IConverter converter, Object result) {
		Preconditions.checkState(converter != null, "Converter not set!");
		if (result instanceof IMultiReturn) return convertMultiResult(converter, (IMultiReturn)result);

		if (multipleReturn) {
			if (result != null && result.getClass().isArray()) return convertArrayResult(converter, result);
			if (result instanceof Collection) return convertCollectionResult(converter, (Collection<?>)result);
		}

		return convertVarResult(converter, result);
	}

	private class CallWrap implements IMethodCall {
		private final Object[] args = new Object[argCount];
		private final boolean[] isSet = new boolean[argCount];
		private final Object target;

		private IConverter converter;

		public CallWrap(Object target) {
			this.target = target;
		}

		private CallWrap setArg(int position, Object value) {
			boolean alreadyAdded = isSet[position];
			Preconditions.checkState(!alreadyAdded, "Trying to set already defined argument %s in method %s", position, method);

			args[position] = value;
			isSet[position] = true;
			return this;
		}

		@Override
		public <T> IMethodCall setEnv(Class<? super T> intf, T instance) {
			if (intf == IConverter.class) this.converter = (IConverter)instance;

			Integer index = envArgsIndices.get(intf);
			if (index != null)
				setArg(index, instance);

			return this;
		}

		private CallWrap setCallArgs(Object[] luaValues) {
			try {
				Iterator<Object> it = Iterators.forArray(luaValues);
				Preconditions.checkState(!it.hasNext() || converter != null, "Converter not set!");
				try {
					for (Argument arg : convertedArgs) {
						Object value = arg.convert(converter, it);
						setArg(arg.javaArgIndex, value);
					}

					Preconditions.checkArgument(!it.hasNext(), "Too many arguments!");
				} catch (ArrayIndexOutOfBoundsException e) {
					throw new IllegalArgumentException(String.format("Invalid Lua parameter count, needs %s, got %s", convertedArgs.size(), luaValues.length));
				}
			} catch (IllegalArgumentException e) {
				throw e;
			} catch (Exception e) {
				throw new AdapterLogicException(e);
			}

			return this;
		}

		private Object[] call() throws Exception {
			for (int i = 0; i < args.length; i++)
				Preconditions.checkState(isSet[i], "Parameter %s value not set", i);

			final Object result;
			try {
				result = method.invoke(target, args);
			} catch (InvocationTargetException e) {
				Throwable wrapper = e.getCause();
				throw Throwables.propagate(wrapper != null? wrapper : e);
			}

			if (voidReturn) return NO_RETURNS;
			final Object[] converted = convertResult(converter, result);
			if (validateReturn) validateResult(converted);
			return converted;
		}

		@Override
		public Object[] call(Object... args) throws Exception {
			setCallArgs(args);
			return call();
		}

	}

	public void defineRequiredEnv(int index, Class<?> type) {
		Class<?> actualType = unnamedEnvArg.remove(index);
		Preconditions.checkState(actualType != null, "Argument at index %s not present or already named, can't bind to type %s", type);
		Preconditions.checkState(actualType.isAssignableFrom(type), "Field %s is expected to be assignable from %s, but has %s", index, type, actualType);
		Integer prevIndex = envArgsIndices.put(type, index);
		if (prevIndex != null) throw new IllegalStateException(String.format("Type %s was already assigned to mandatory argument: prev index: %d, new index: %d", type, prevIndex, index));
	}

	public void defineTargetArg(int index, Class<?> requiredType) {
		Class<?> actualType = unnamedEnvArg.remove(index);
		Preconditions.checkState(actualType != null, "Argument at index %s not present or already named, can't use as target", index);
		defineTargetArg(index, requiredType, actualType);
	}

	public boolean tryDefineTargetArg(int index, Class<?> requiredType) {
		Class<?> actualType = unnamedEnvArg.remove(index);
		if (actualType == null) return false;
		defineTargetArg(index, requiredType, actualType);
		return true;
	}

	private void defineTargetArg(int index, Class<?> requiredType, Class<?> actualType) {
		Preconditions.checkState(actualType.isAssignableFrom(requiredType), "Field %s is expected to be assignable from %s, but has %s", index, requiredType, actualType);
		if (targetArg.isPresent())
			throw new IllegalStateException(String.format("Target arg already set: prev (index: %d, type: %s), new (index: %d, type: %s)", requiredType, targetArg.get().index, targetArg.get().type, index, requiredType));

		this.targetArg = Optional.of(new IndexTypePair(index, requiredType));
	}

	private Set<Class<?>> requiredEnvs() {
		Preconditions.checkState(unnamedEnvArg.isEmpty(), "Undefined arguments left: %s", unnamedEnvArg);

		final ImmutableSet.Builder<Class<?>> result = ImmutableSet.builder();
		result.addAll(envArgsIndices.keySet());
		if (requiresConverter) result.add(IConverter.class);
		return result.build();
	}

	private abstract class MethodCallerBase implements IMethodCaller {
		private final Set<Class<?>> requiredEnvs;

		public MethodCallerBase() {
			this.requiredEnvs = requiredEnvs();
		}

		@Override
		public Set<Class<?>> requiredEnvArgs() {
			return requiredEnvs;
		}
	}

	public IMethodCaller createBoundMethodCaller(final Object boundTarget) {
		final Class<?> expectedType = method.getDeclaringClass();
		Preconditions.checkState(boundTarget == null || expectedType.isInstance(boundTarget), "Expected instance of type %s (or null), got %s", expectedType, boundTarget);

		if (this.targetArg.isPresent()) {
			final IndexTypePair targetArg = this.targetArg.get();

			return new MethodCallerBase() {
				@Override
				public IMethodCall startCall(Object target) {
					Preconditions.checkState(target == null || targetArg.type.isInstance(target), "Expected instance of type %s (or null), got %s", targetArg.type, target);
					return new CallWrap(boundTarget).setArg(targetArg.index, target);
				}
			};
		} else {
			return new MethodCallerBase() {
				@Override
				public IMethodCall startCall(Object target) {
					return new CallWrap(boundTarget);
				}
			};
		}
	}

	public IMethodCaller createUnboundMethodCaller() {
		Preconditions.checkState(!this.targetArg.isPresent(), "Unbound caller for method %s can't have target arg", method); // rather pointless, but harmless

		return new MethodCallerBase() {
			@Override
			public IMethodCall startCall(Object target) {
				return new CallWrap(target);
			}
		};
	}

	public IMethodDescription getMethodDescription() {
		return new IMethodDescription() {
			@Override
			public List<String> getNames() {
				return names;
			}

			@Override
			public String source() {
				return source;
			}

			@Override
			public List<IArgumentDescription> arguments() {
				List<? extends IArgumentDescription> cast = convertedArgs;
				return ImmutableList.copyOf(cast);
			}

			@Override
			public IScriptType returnTypes() {
				return wrappedReturn;
			}

			@Override
			public String description() {
				return description;
			}
		};
	}

}
