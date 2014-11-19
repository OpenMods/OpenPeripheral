package openperipheral.adapter.method;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;

import openmods.Log;
import openmods.reflection.TypeUtils;
import openmods.utils.AnnotationMap;
import openperipheral.TypeConversionRegistry;
import openperipheral.adapter.AdapterLogicException;
import openperipheral.adapter.IDescriptable;
import openperipheral.api.*;

import org.apache.logging.log4j.Level;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.*;

public class MethodDeclaration implements IDescriptable {

	public static class ArgumentDefinitionException extends IllegalStateException {
		private static final long serialVersionUID = -6428721405547878927L;

		public ArgumentDefinitionException(int argument, Throwable cause) {
			super(String.format("Failed to parse annotations on argument %d", argument), cause);
		}

	}

	private final List<String> names;
	private final String source;
	private final Method method;
	private final String description;
	private final LuaReturnType[] returnTypes;

	private final boolean validateReturn;

	private final BiMap<String, Integer> namedArgs = HashBiMap.create();
	private final Set<String> allowedNames = Sets.newHashSet();

	private final List<Class<?>> javaArgs;
	private final List<Argument> luaArgs;

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
		JAVA_REQUIRED,
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

		if (validateReturn) validateResultCount();

		final Class<?> methodArgs[] = method.getParameterTypes();
		final boolean isVarArg = method.isVarArgs();

		ImmutableList.Builder<Argument> luaArgs = ImmutableList.builder();
		ImmutableList.Builder<Class<?>> javaArgs = ImmutableList.builder();

		ArgParseState state = ArgParseState.JAVA_REQUIRED;

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
					Preconditions.checkState(state == ArgParseState.JAVA_OPTIONAL || state == ArgParseState.JAVA_REQUIRED, "Unannotated arg in Lua part (perhaps missing @Arg annotation?)");
					Preconditions.checkState(!optionalStart, "@Optionals does not work for java arguments");

					if (envArg != null) {
						Preconditions.checkState(state == ArgParseState.JAVA_OPTIONAL || state == ArgParseState.JAVA_REQUIRED, "@Env annotation used in Lua part of arguments");
						namedArgs.put(envArg.value(), argIndex);
						state = ArgParseState.JAVA_OPTIONAL;
					} else {
						Preconditions.checkState(state == ArgParseState.JAVA_REQUIRED, "Unnamed arguments must be declared before named ones");
					}
					javaArgs.add(argType);
				}
			} catch (Throwable t) {
				throw new ArgumentDefinitionException(argIndex, t);
			}
		}

		this.luaArgs = luaArgs.build();
		this.javaArgs = javaArgs.build();
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

		if (returnLength > 1) {
			Preconditions.checkArgument(javaReturn == IMultiReturn.class, "Method '%s' declared more than one Lua result, but returns single '%s' instead of '%s'", method, javaReturn, IMultiReturn.class);
		}
	}

	private Object[] validateResult(Object... result) {
		for (int i = 0; i < result.length; i++)
			result[i] = TypeConversionRegistry.INSTANCE.toLua(result[i]);

		if (validateReturn) {
			if (returnTypes.length == 0) {
				Preconditions.checkArgument(result.length == 1 && result[0] == null, "Returning value from null method");
			} else {
				Preconditions.checkArgument(result.length == returnTypes.length, "Returning invalid number of values from method %s, expected %s, got %s", method, returnTypes.length, result.length);
				for (int i = 0; i < result.length; i++) {
					final LuaReturnType expected = returnTypes[i];
					final Class<?> expectedType = expected.getJavaType();
					final Object got = result[i];
					Preconditions.checkArgument(got == null || expectedType.isInstance(got) || TypeUtils.compareTypes(expectedType, got.getClass()), "Invalid type of return value %s: expected %s, got %s", i, expected, got);
				}
			}
		}

		return result;
	}

	public class CallWrap implements Callable<Object[]> {
		private final Object[] args = new Object[javaArgs.size() + luaArgs.size()];
		private final Set<Integer> isSet = Sets.newHashSet();
		private final Object target;

		public CallWrap(Object target) {
			this.target = target;
		}

		private CallWrap setArg(int position, Object value) {
			boolean newlyAdded = isSet.add(position);
			Preconditions.checkState(newlyAdded, "Trying to set already defined argument %s in method %s", position, method);
			args[position] = value;
			return this;
		}

		public CallWrap setJavaArg(String name, Object value) {
			Integer position = namedArgs.get(name);
			if (position != null) setArg(position, value);
			return this;
		}

		public CallWrap setLuaArgs(Object[] luaValues) {
			try {
				Iterator<Object> it = Iterators.forArray(luaValues);
				try {
					for (Argument arg : luaArgs) {
						Object value = arg.convert(it);
						setArg(arg.javaArgIndex, value);
					}

					Preconditions.checkState(!it.hasNext(), "Too many arguments!");
				} catch (ArrayIndexOutOfBoundsException e) {
					Log.log(Level.TRACE, e, "Trying to access arg index, args = %s", Arrays.toString(luaValues));
					throw new IllegalArgumentException(String.format("Invalid Lua parameter count, needs %s, got %s", luaArgs.size(), luaValues.length));
				}
			} catch (Exception e) {
				throw new AdapterLogicException(e);
			}

			return this;
		}

		@Override
		public Object[] call() throws Exception {
			for (int i = 0; i < args.length; i++)
				Preconditions.checkState(isSet.contains(i), "Parameter %s value not set", i);

			Object result;
			try {
				result = method.invoke(target, args);
			} catch (InvocationTargetException e) {
				Throwable wrapper = e.getCause();
				throw Throwables.propagate(wrapper != null? wrapper : e);
			}

			if (result instanceof IMultiReturn) return validateResult(((IMultiReturn)result).getObjects());
			else return validateResult(result);
		}
	}

	public CallWrap createWrapper(Object target) {
		return new CallWrap(target);
	}

	public void setDefaultArgName(int index, String name) {
		if (index >= javaArgs.size()) {
			// probably in Lua args already
			return;
		}

		if (namedArgs.containsValue(index)) {
			// arg already named, ignore
			return;
		}

		Integer prev = namedArgs.put(name, index);
		Preconditions.checkArgument(prev == null || prev == index, "Trying to replace '%s' mapping from  %s, got %s", name, prev, index);
	}

	public void declareJavaArgType(String name, Class<?> cls) {
		allowedNames.add(name);
		Integer index = namedArgs.get(name);
		if (index != null) {
			final Class<?> expected = javaArgs.get(index);
			Preconditions.checkArgument(expected.isAssignableFrom(cls), "Invalid argument type in method %s, was %s, got %s", method, expected, cls);
		}
	}

	public void validate() {
		Set<String> unknown = Sets.difference(namedArgs.keySet(), allowedNames);
		Preconditions.checkState(unknown.isEmpty(), "Unknown named arg(s) %s in method '%s'. Allowed args: %s", unknown, method, allowedNames);

		Set<Integer> needed = Sets.newHashSet();
		for (int i = 0; i < javaArgs.size(); i++)
			needed.add(i);

		Set<Integer> named = Sets.newHashSet(namedArgs.values());
		Set<Integer> missing = Sets.difference(needed, named);
		Preconditions.checkState(missing.isEmpty(), "Arguments %s from method %s are not named", missing, method);

		Set<Integer> extra = Sets.difference(named, needed);
		Preconditions.checkState(missing.isEmpty(), "Lua arguments %s from method %s are named", extra, method);
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
}
