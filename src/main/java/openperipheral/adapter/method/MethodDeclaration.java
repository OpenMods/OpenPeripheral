package openperipheral.adapter.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

import openmods.reflection.TypeUtils;
import openmods.utils.AnnotationMap;
import openperipheral.adapter.AdapterLogicException;
import openperipheral.adapter.IMethodCall;
import openperipheral.adapter.IMethodDescription;
import openperipheral.adapter.types.IType;
import openperipheral.adapter.types.TypeHelper;
import openperipheral.api.Constants;
import openperipheral.api.adapter.method.*;
import openperipheral.api.converter.IConverter;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.*;
import com.google.common.reflect.TypeToken;

public class MethodDeclaration implements IMethodDescription {

	public static class ArgumentDefinitionException extends IllegalStateException {
		private static final long serialVersionUID = -6428721405547878927L;

		public ArgumentDefinitionException(int argument, Throwable cause) {
			super(String.format("Failed to parse annotations on argument %d", argument), cause);
		}
	}

	private static class EnvArg {
		public final Class<?> cls;
		public final int index;

		public EnvArg(Class<?> cls, int index) {
			this.cls = cls;
			this.index = index;
		}
	}

	private final List<String> names;
	private final String source;
	private final Method method;
	private final String description;
	private final List<ReturnType> returnTypes;
	private final IType wrappedReturn;

	private final boolean validateReturn;

	private final boolean multipleReturn;

	private final Map<Integer, Class<?>> unnamedEnvArg = Maps.newHashMap();

	private final Map<String, EnvArg> envArgs = Maps.newHashMap();

	private final List<Argument> callArgs = Lists.newArrayList();

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

	private static enum ArgParseState {
		ENV_UNNAMED,
		ENV_NAMED,
		ARG_REQUIRED,
		ARG_OPTIONAL,

	}

	public MethodDeclaration(Method method, ScriptCallable meta, String source) {
		this.method = method;
		this.source = source;

		this.names = getNames(method, meta);

		this.description = meta.description();
		this.returnTypes = ImmutableList.copyOf(meta.returnTypes());
		this.validateReturn = meta.validateReturn();

		this.multipleReturn = method.isAnnotationPresent(MultipleReturn.class);

		this.wrappedReturn = TypeHelper.createFromReturn(returnTypes);

		if (validateReturn) validateResultCount();

		final Type methodArgs[] = method.getGenericParameterTypes();
		final boolean isVarArg = method.isVarArgs();

		ArgParseState state = ArgParseState.ENV_UNNAMED;

		final Annotation[][] argsAnnotations = method.getParameterAnnotations();
		for (int argIndex = 0; argIndex < methodArgs.length; argIndex++) {
			try {
				final TypeToken<?> argType = TypeToken.of(methodArgs[argIndex]);

				AnnotationMap argAnnotations = new AnnotationMap(argsAnnotations[argIndex]);

				boolean optionalStart = argAnnotations.get(Optionals.class) != null;

				Env envArg = argAnnotations.get(Env.class);
				Arg luaArg = argAnnotations.get(Arg.class);

				Preconditions.checkState(envArg == null || luaArg == null, "@Arg and @Env are mutually exclusive");
				if (luaArg != null) {

					if (state != ArgParseState.ARG_OPTIONAL) state = ArgParseState.ARG_REQUIRED;

					if (optionalStart) {
						Preconditions.checkState(state != ArgParseState.ENV_NAMED, "@Optional used more than once");
						state = ArgParseState.ARG_OPTIONAL;
					}

					boolean isLastArg = argIndex == (methodArgs.length - 1);

					ArgumentBuilder builder = new ArgumentBuilder();
					builder.setVararg(isLastArg && isVarArg);
					builder.setOptional(state == ArgParseState.ARG_OPTIONAL);
					builder.setNullable(luaArg.isNullable());

					final Argument arg = builder.build(luaArg.name(), luaArg.description(), luaArg.type(), argType, argIndex);
					callArgs.add(arg);
				} else {
					Preconditions.checkState(state == ArgParseState.ENV_NAMED || state == ArgParseState.ENV_UNNAMED, "Unannotated arg in script part (perhaps missing @Arg annotation?)");
					Preconditions.checkState(!optionalStart, "@Optionals does not work for env arguments");

					Class<?> rawArgType = argType.getRawType();
					if (envArg != null) {
						Preconditions.checkState(state == ArgParseState.ENV_NAMED || state == ArgParseState.ENV_UNNAMED, "@Env annotation used in script part of arguments");
						final String envName = envArg.value();
						EnvArg prev = envArgs.put(envName, new EnvArg(rawArgType, argIndex));
						if (prev != null) { throw new IllegalStateException(String.format("Conflict on name %s, args: %s, %s", envArg, prev.index, argIndex)); }
						state = ArgParseState.ENV_NAMED;
					} else {
						Preconditions.checkState(state == ArgParseState.ENV_UNNAMED, "Unnamed env cannot occur after named");
						unnamedEnvArg.put(argIndex, rawArgType);
					}
				}
			} catch (Throwable t) {
				throw new ArgumentDefinitionException(argIndex, t);
			}
		}

		this.argCount = unnamedEnvArg.size() + envArgs.size() + callArgs.size();
		Preconditions.checkState(this.argCount == methodArgs.length, "Internal error for method %s", method);
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
		Preconditions.checkArgument(actual == null || expectedJava.isInstance(actual) || TypeUtils.compareTypes(expectedJava, actual.getClass()), "Invalid type of return value %s: expected %s, got %s", argIndex, expected, actual);
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
		public IMethodCall setEnv(String name, Object value) {
			if (Constants.ARG_CONVERTER.equals(name)) this.converter = (IConverter)value;

			EnvArg arg = envArgs.get(name);
			if (arg != null) {
				Preconditions.checkState(value == null || arg.cls.isInstance(value),
						"Object of type %s cannot be used as argument %s (name: %s) in method %s",
						value != null? value.getClass() : "<null>", arg.index, name, method);
				setArg(arg.index, value);
			}
			return this;
		}

		private CallWrap setCallArgs(Object[] luaValues) {
			Preconditions.checkState(converter != null, "Converter not set!");
			try {
				Iterator<Object> it = Iterators.forArray(luaValues);
				try {
					for (Argument arg : callArgs) {
						Object value = arg.convert(converter, it);
						setArg(arg.javaArgIndex, value);
					}

					Preconditions.checkArgument(!it.hasNext(), "Too many arguments!");
				} catch (ArrayIndexOutOfBoundsException e) {
					throw new IllegalArgumentException(String.format("Invalid Lua parameter count, needs %s, got %s", callArgs.size(), luaValues.length));
				}
			} catch (IllegalArgumentException e) {
				throw e;
			} catch (Exception e) {
				throw new AdapterLogicException(e);
			}

			return this;
		}

		private Object[] call() throws Exception {
			Preconditions.checkState(converter != null, "Converter not set!");
			for (int i = 0; i < args.length; i++)
				Preconditions.checkState(isSet[i], "Parameter %s value not set", i);

			final Object result;
			try {
				result = method.invoke(target, args);
			} catch (InvocationTargetException e) {
				Throwable wrapper = e.getCause();
				throw Throwables.propagate(wrapper != null? wrapper : e);
			}

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

	public IMethodCall startCall(Object target) {
		return new CallWrap(target);
	}

	public void nameEnv(int index, String name, Class<?> expectedType) {
		Class<?> actualType = unnamedEnvArg.remove(index);
		Preconditions.checkState(actualType != null, "Argument at index %s not present or already named, can't name as %s");
		Preconditions.checkState(actualType.isAssignableFrom(expectedType), "Field %s (new name: %s) is expected to be %s, but has %s", index, name, expectedType, actualType);
		EnvArg prev = envArgs.put(name, new EnvArg(actualType, index));
		if (prev != null) throw new IllegalStateException(String.format("Name %s is already used: prev index: %d, new index: %d", name, prev.index, index));
	}

	public void verifyAllParamsNamed() {
		Preconditions.checkState(unnamedEnvArg.isEmpty(), "Env parameters not named: %s", unnamedEnvArg);
	}

	public void validateUnnamedEnvArgs(Class<?>... providedArgs) {
		Preconditions.checkState(providedArgs.length == unnamedEnvArg.size());
		for (int i = 0; i < providedArgs.length; i++) {
			final Class<?> needed = unnamedEnvArg.get(i);
			final Class<?> provided = providedArgs[i];
			Preconditions.checkState(needed.isAssignableFrom(provided),
					"Argument %s needs type %s, but %s provided", i, needed, provided);
		}
	}

	public void validateEnvArgs(Map<String, Class<?>> providedArgs) {
		for (Map.Entry<String, EnvArg> e : envArgs.entrySet()) {
			final EnvArg needed = e.getValue();
			final String name = e.getKey();
			final Class<?> provided = providedArgs.get(name);
			Preconditions.checkState(provided != null, "Method needs argument named %s (position %s) but it's not provided",
					name, needed.index);

			final Class<?> neededCls = needed.cls;
			Preconditions.checkState(neededCls.isAssignableFrom(provided),
					"Method needs argument named %s (position %s) of type %s, but %s was provided",
					name, needed.index, neededCls, provided
					);
		}
	}

	public Map<String, Class<?>> getOptionalArgs() {
		Map<String, Class<?>> result = Maps.newHashMap();

		for (Map.Entry<String, EnvArg> e : envArgs.entrySet())
			result.put(e.getKey(), e.getValue().cls);

		return result;
	}

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
		List<? extends IArgumentDescription> cast = callArgs;
		return ImmutableList.copyOf(cast);
	}

	@Override
	public IType returnTypes() {
		return wrappedReturn;
	}

	@Override
	public Set<String> attributes() {
		return Sets.newHashSet();
	}

	@Override
	public String description() {
		return description;
	}
}
