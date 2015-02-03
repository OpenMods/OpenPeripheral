package openperipheral.adapter.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import openmods.Log;
import openmods.reflection.TypeUtils;
import openmods.utils.AnnotationMap;
import openperipheral.adapter.AdapterLogicException;
import openperipheral.adapter.IDescriptable;
import openperipheral.adapter.IMethodCall;
import openperipheral.api.Constants;
import openperipheral.api.adapter.method.*;
import openperipheral.api.converter.IConverter;

import org.apache.logging.log4j.Level;

import com.google.common.base.*;
import com.google.common.collect.*;

public class MethodDeclaration implements IDescriptable {

	public static class ArgumentDefinitionException extends IllegalStateException {
		private static final long serialVersionUID = -6428721405547878927L;

		public ArgumentDefinitionException(int argument, Throwable cause) {
			super(String.format("Failed to parse annotations on argument %d", argument), cause);
		}
	}

	private static class OptionalArg {
		public final Class<?> cls;
		public final int index;

		public OptionalArg(Class<?> cls, int index) {
			this.cls = cls;
			this.index = index;
		}
	}

	private final List<String> names;
	private final String source;
	private final Method method;
	private final String description;
	private final LuaReturnType[] returnTypes;

	private final boolean validateReturn;

	private final boolean multipleReturn;

	private final List<Class<?>> positionalArgs = Lists.newArrayList();

	private final Map<String, OptionalArg> optionalArgs = Maps.newHashMap();

	private final List<Argument> luaArgs = Lists.newArrayList();

	private final int argCount;

	private static List<String> getNames(Method method, LuaCallable meta) {
		ImmutableList.Builder<String> names = ImmutableList.builder();

		String mainName = meta.name();

		if (LuaCallable.USE_METHOD_NAME.equals(mainName)) names.add(method.getName());
		else names.add(mainName);

		Alias alias = method.getAnnotation(Alias.class);
		if (alias != null) names.add(alias.value());
		return names.build();
	}

	private static enum ArgParseState {
		JAVA_POSITIONAL,
		JAVA_OPTIONAL,
		LUA_REQUIRED,
		LUA_OPTIONAL,

	}

	public MethodDeclaration(Method method, LuaCallable meta, String source) {
		this.method = method;
		this.source = source;

		this.names = getNames(method, meta);

		this.description = meta.description();
		this.returnTypes = meta.returnTypes();
		this.validateReturn = meta.validateReturn();

		this.multipleReturn = method.isAnnotationPresent(MultipleReturn.class);

		if (validateReturn) validateResultCount();

		final Class<?> methodArgs[] = method.getParameterTypes();
		final boolean isVarArg = method.isVarArgs();

		ArgParseState state = ArgParseState.JAVA_POSITIONAL;

		final Annotation[][] argsAnnotations = method.getParameterAnnotations();
		for (int argIndex = 0; argIndex < methodArgs.length; argIndex++) {
			try {
				final Class<?> argType = methodArgs[argIndex];

				AnnotationMap argAnnotations = new AnnotationMap(argsAnnotations[argIndex]);

				boolean optionalStart = argAnnotations.get(Optionals.class) != null;

				Env envArg = argAnnotations.get(Env.class);
				Arg luaArg = argAnnotations.get(Arg.class);

				Preconditions.checkState(envArg == null || luaArg == null, "@Arg and @Env are mutually exclusive");
				if (luaArg != null) {

					if (state != ArgParseState.LUA_OPTIONAL) state = ArgParseState.LUA_REQUIRED;

					if (optionalStart) {
						Preconditions.checkState(state != ArgParseState.JAVA_OPTIONAL, "@Optional used more than once");
						state = ArgParseState.LUA_OPTIONAL;
					}

					boolean isLastArg = argIndex == (methodArgs.length - 1);

					ArgumentBuilder builder = new ArgumentBuilder();
					builder.setVararg(isLastArg && isVarArg);
					builder.setOptional(state == ArgParseState.LUA_OPTIONAL);
					builder.setNullable(luaArg.isNullable());

					final Argument arg = builder.build(luaArg.name(), luaArg.description(), luaArg.type(), argType, argIndex);
					luaArgs.add(arg);
				} else {
					Preconditions.checkState(state == ArgParseState.JAVA_OPTIONAL || state == ArgParseState.JAVA_POSITIONAL, "Unannotated arg in Lua part (perhaps missing @Arg annotation?)");
					Preconditions.checkState(!optionalStart, "@Optionals does not work for java arguments");

					if (envArg != null) {
						Preconditions.checkState(state == ArgParseState.JAVA_OPTIONAL || state == ArgParseState.JAVA_POSITIONAL, "@Env annotation used in Lua part of arguments");
						final String envName = envArg.value();
						OptionalArg prev = optionalArgs.put(envName, new OptionalArg(argType, argIndex));
						if (prev != null) { throw new IllegalStateException(String.format("Conflict on name %s, args: %s, %s", envArg, prev.index, argIndex)); }
						state = ArgParseState.JAVA_OPTIONAL;
					} else {
						Preconditions.checkState(state == ArgParseState.JAVA_POSITIONAL, "Unnamed arg cannot occur after named");
						positionalArgs.add(argType);
					}
				}
			} catch (Throwable t) {
				throw new ArgumentDefinitionException(argIndex, t);
			}
		}

		this.argCount = positionalArgs.size() + optionalArgs.size() + luaArgs.size();
		Preconditions.checkState(this.argCount == methodArgs.length, "Internal error for method %s", method);
	}

	private void validateResultCount() {
		Class<?> javaReturn = method.getReturnType();

		final int returnLength = returnTypes.length;

		for (LuaReturnType t : returnTypes) {
			Preconditions.checkArgument(t != LuaReturnType.VOID, "Method '%s' declares Void as return type. Use empty list instead.", method);
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

	private static void checkReturnType(int argIndex, LuaReturnType expected, Object actual) {
		final Class<?> expectedJava = expected.getJavaType();
		Preconditions.checkArgument(actual == null || expectedJava.isInstance(actual) || TypeUtils.compareTypes(expectedJava, actual.getClass()), "Invalid type of return value %s: expected %s, got %s", argIndex, expected, actual);
	}

	private void validateResult(Object... result) {
		if (returnTypes.length == 0) {
			Preconditions.checkArgument(result.length == 1 && result[0] == null, "Returning value from null method");
		} else {
			Preconditions.checkArgument(result.length == returnTypes.length, "Returning invalid number of values from method %s, expected %s, got %s", method, returnTypes.length, result.length);
			for (int i = 0; i < result.length; i++)
				checkReturnType(i, returnTypes[i], result[i]);
		}
	}

	private static Object[] convertMultiResult(IConverter converter, IMultiReturn result) {
		return convertVarResult(converter, result.getObjects());
	}

	private static Object[] convertCollectionResult(IConverter converter, Collection<?> result) {
		Object[] tmp = new Object[result.size()];
		int i = 0;
		for (Object o : result)
			tmp[i++] = converter.toLua(o);

		return tmp;
	}

	private static Object[] convertArrayResult(IConverter converter, Object array) {
		int length = Array.getLength(array);
		Object[] result = new Object[length];

		for (int i = 0; i < length; i++)
			result[i] = converter.toLua(Array.get(array, i));

		return result;
	}

	private static Object[] convertVarResult(IConverter converter, Object... result) {
		for (int i = 0; i < result.length; i++)
			result[i] = converter.toLua(result[i]);

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
		public IMethodCall setOptionalArg(String name, Object value) {
			if (Constants.ARG_CONVERTER.equals(name)) this.converter = (IConverter)value;

			OptionalArg arg = optionalArgs.get(name);
			if (arg != null) {
				Preconditions.checkState(value == null || arg.cls.isInstance(value),
						"Object of type %s cannot be used as argument %s (name: %s) in method %s",
						value != null? value.getClass() : "<null>", arg.index, name, method);
				setArg(arg.index, value);
			}
			return this;
		}

		@Override
		public IMethodCall setPositionalArg(int index, Object value) {
			Preconditions.checkElementIndex(index, positionalArgs.size(), "argument index");
			Preconditions.checkState(value == null || positionalArgs.get(index).isInstance(value),
					"Object of type %s cannot be used as argument %s in method %s",
					value != null? value.getClass() : "<null>", index, method);

			setArg(index, value);
			return this;
		}

		private CallWrap setCallArgs(Object[] luaValues) {
			Preconditions.checkState(converter != null, "Converter not set!");
			try {
				Iterator<Object> it = Iterators.forArray(luaValues);
				try {
					for (Argument arg : luaArgs) {
						Object value = arg.convert(converter, it);
						setArg(arg.javaArgIndex, value);
					}

					Preconditions.checkArgument(!it.hasNext(), "Too many arguments!");
				} catch (ArrayIndexOutOfBoundsException e) {
					Log.log(Level.TRACE, e, "Trying to access arg index, args = %s", Arrays.toString(luaValues));
					throw new IllegalArgumentException(String.format("Invalid Lua parameter count, needs %s, got %s", luaArgs.size(), luaValues.length));
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
		public Object[] call(Object[] args) throws Exception {
			setCallArgs(args);
			return call();
		}
	}

	public IMethodCall startCall(Object target) {
		return new CallWrap(target);
	}

	public void validatePositionalArgs(Class<?>... providedArgs) {
		Preconditions.checkState(providedArgs.length == positionalArgs.size());
		for (int i = 0; i < providedArgs.length; i++) {
			final Class<?> needed = positionalArgs.get(i);
			final Class<?> provided = providedArgs[i];
			Preconditions.checkState(needed.isAssignableFrom(provided),
					"Argument %s needs type %s, but %s provided", i, needed, provided);
		}
	}

	public void validateOptionalArgs(Map<String, Class<?>> providedArgs) {
		for (Map.Entry<String, OptionalArg> e : optionalArgs.entrySet()) {
			final OptionalArg needed = e.getValue();
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

		for (Map.Entry<String, OptionalArg> e : optionalArgs.entrySet())
			result.put(e.getKey(), e.getValue().cls);

		return result;
	}

	@Override
	public Map<String, Object> describe() {
		Map<String, Object> result = Maps.newHashMap();
		result.put(IDescriptable.DESCRIPTION, description);
		result.put(IDescriptable.SOURCE, source);

		{
			List<String> returns = Lists.newArrayList();
			for (LuaReturnType t : returnTypes)
				returns.add(t.toString());
			result.put(IDescriptable.RETURN_TYPES, returns);
		}

		{
			List<Map<String, Object>> args = Lists.newArrayList();
			for (Argument arg : luaArgs)
				args.add(arg.describe());
			result.put(IDescriptable.ARGS, args);
		}
		return result;
	}

	@Override
	public String signature() {
		return "(" + Joiner.on(",").join(luaArgs) + ")";
	}

	@Override
	public List<String> getNames() {
		return names;
	}

	public Class<?>[] getLuaArgTypes() {
		Class<?>[] result = new Class<?>[luaArgs.size()];

		int index = 0;
		for (Argument arg : luaArgs)
			result[index++] = arg.javaType;

		return result;
	}

	@Override
	public String source() {
		return source;
	}

	@Override
	public String doc() {
		// function(arg:type[, optionArg:type]):resultType -- Description
		List<String> args = Lists.newArrayList();

		for (Argument arg : luaArgs)
			args.add(arg.name + ":" + arg.doc());

		List<String> returns = Lists.newArrayList();

		for (LuaReturnType r : returnTypes)
			returns.add(r.getName());

		String ret = returns.size() == 1? returns.get(0) : ("(" + Joiner.on(',').join(returns) + ")");

		String result = String.format("function(%s):%s", Joiner.on(',').join(args), ret);

		if (!Strings.isNullOrEmpty(description)) result += " -- " + description;

		return result;
	}
}
