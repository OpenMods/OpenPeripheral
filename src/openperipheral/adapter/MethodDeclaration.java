package openperipheral.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import openmods.Log;
import openmods.utils.AnnotationMap;
import openmods.utils.ReflectionHelper;
import openperipheral.TypeConversionRegistry;
import openperipheral.api.*;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;

public class MethodDeclaration {

	public static class ConvertedArgument {
		public final String name;
		public final String description;
		public final LuaType luaType;
		public final Class<?> javaType;
		public final boolean isVarArg;
		private final boolean isNullable;
		private final boolean isOptional;
		private final int javaArgIndex;

		private ConvertedArgument(Arg arg, Class<?> javaClass, int javaArgIndex, boolean isVarArg, boolean isOptional) {
			this.name = arg.name();
			this.description = arg.description();
			this.luaType = arg.type();
			if (isVarArg) this.javaType = javaClass.getComponentType();
			else this.javaType = javaClass;
			this.isVarArg = isVarArg;
			this.isOptional = isOptional;
			this.isNullable = arg.isNullable() || isOptional;
			this.javaArgIndex = javaArgIndex;
		}

		public Object convert(Object o) {
			if (o == null) return null;
			Object converted = TypeConversionRegistry.fromLua(o, javaType);
			Preconditions.checkNotNull(converted, "Failed to convert arg '%s' value '%s' to '%s'", name, o, javaType.getSimpleName());
			return converted;
		}

		public Map<String, Object> describe() {
			Map<String, Object> result = Maps.newHashMap();
			result.put("type", luaType.toString());
			result.put("name", name);
			result.put("description", description);
			result.put("vararg", isVarArg);
			result.put("optional", isOptional);
			result.put("nullable", isNullable);
			return result;
		}

		@Override
		public String toString() {
			return isVarArg? (name + "...") : name;
		}
	}

	public final String name;
	private final Method method;
	private final String description;
	private final LuaType[] returnTypes;

	private final boolean validateReturn;

	private final Map<String, Integer> namedArgs = Maps.newHashMap();
	private final List<Class<?>> javaArgs;
	private final List<ConvertedArgument> luaArgs;

	private ConvertedArgument createLuaArg(AnnotationMap annotations, Class<?> javaArgType, int index, boolean forceOptional, boolean isVarArg) {
		Arg arg = annotations.get(Arg.class);
		Preconditions.checkNotNull(arg);

		final boolean isOptional = forceOptional || annotations.get(Optionals.class) != null;

		ConvertedArgument result = new ConvertedArgument(arg, javaArgType, index, isVarArg, isOptional);

		Preconditions.checkArgument(!(result.javaType.isPrimitive() && result.isNullable),
				"In method %s arg %s has primitive type %s, but is marked nullable or optional",
				method, index, result.javaType);

		return result;
	}

	public MethodDeclaration(Method method, LuaMethod luaMethod) {
		this.method = method;

		String luaName = luaMethod.name();
		this.name = (LuaMethod.USE_METHOD_NAME.equals(luaName))? method.getName() : luaName;
		this.description = luaMethod.description();
		this.returnTypes = new LuaType[] { luaMethod.returnType() };
		this.validateReturn = false;

		final Class<?> methodArgs[] = method.getParameterTypes();
		final Arg declaredLuaArgs[] = luaMethod.args();
		final Annotation[][] argsAnnotations = method.getParameterAnnotations();
		final int luaArgsStart = methodArgs.length - declaredLuaArgs.length;
		final boolean isVarArg = method.isVarArgs();

		Preconditions.checkArgument(luaArgsStart >= 0, "Method %s has less arguments than declared", method);

		boolean isOptional = false;

		ImmutableList.Builder<ConvertedArgument> luaArgs = ImmutableList.builder();
		for (int arg = 0; arg < declaredLuaArgs.length; arg++) {
			boolean isLastArg = arg == (declaredLuaArgs.length - 1);

			AnnotationMap annotations = new AnnotationMap(argsAnnotations[arg]);
			annotations.put(declaredLuaArgs[arg]);
			int javaArgIndex = luaArgsStart + arg;
			ConvertedArgument luaArg = createLuaArg(annotations, methodArgs[javaArgIndex], javaArgIndex, isOptional, isLastArg && isVarArg);
			luaArgs.add(luaArg);
			isOptional |= luaArg.isOptional;
		}

		this.luaArgs = luaArgs.build();
		this.javaArgs = ImmutableList.copyOf(Arrays.copyOf(methodArgs, luaArgsStart));

		for (int arg = 0; arg < luaArgsStart; arg++) {
			AnnotationMap annotations = new AnnotationMap(argsAnnotations[arg]);
			Named named = annotations.get(Named.class);
			if (named != null) namedArgs.put(named.value(), arg);
			Preconditions.checkState(annotations.get(Optionals.class) == null, "@Optionals does not work for java arguments (method %s)", method);
		}
	}

	public MethodDeclaration(Method method, LuaCallable meta) {
		this.method = method;

		String luaName = meta.name();
		this.name = (LuaCallable.USE_METHOD_NAME.equals(luaName))? method.getName() : luaName;
		this.description = meta.description();
		this.returnTypes = meta.returnTypes();
		this.validateReturn = meta.validateReturn();

		if (validateReturn) validateResultCount();

		final Class<?> methodArgs[] = method.getParameterTypes();
		final Annotation[][] argsAnnotations = method.getParameterAnnotations();
		final boolean isVarArg = method.isVarArgs();

		ImmutableList.Builder<ConvertedArgument> luaArgs = ImmutableList.builder();
		ImmutableList.Builder<Class<?>> javaArgs = ImmutableList.builder();
		boolean isInLuaArgs = false;
		boolean isOptional = false;

		for (int i = 0; i < methodArgs.length; i++) {
			boolean isLastArg = i == (methodArgs.length - 1);
			final Class<?> cls = methodArgs[i];

			AnnotationMap annotations = new AnnotationMap(argsAnnotations[i]);

			boolean isLuaArg = false;

			Arg tmp = annotations.get(Arg.class);
			if (tmp != null) {
				ConvertedArgument luaArg = createLuaArg(annotations, cls, i, isOptional, isLastArg && isVarArg);
				luaArgs.add(luaArg);
				isOptional |= luaArg.isOptional;
				isLuaArg = true;
				isInLuaArgs = true;
			}

			Preconditions.checkState(!isInLuaArgs || isLuaArg, "Argument %s in method %s look like Java arg, but is in Lua part (perhaps missing Arg annotation?)", i, method);

			Named named = annotations.get(Named.class);
			if (named != null) {
				Preconditions.checkState(!isInLuaArgs, "Argument %s in method %s is Lua arg, but has Named annotation", i, method);
				namedArgs.put(named.value(), i);
			}

			Preconditions.checkState(isInLuaArgs || annotations.get(Optionals.class) == null, "@Optionals does not work for java arguments (method %s)", method);

			if (!isLuaArg) javaArgs.add(cls);
		}

		this.luaArgs = luaArgs.build();
		this.javaArgs = javaArgs.build();
	}

	private void validateResultCount() {
		Class<?> javaReturn = method.getReturnType();

		final int returnLength = returnTypes.length;

		for (LuaType t : returnTypes)
			Preconditions.checkArgument(t != LuaType.VOID, "Method '%s' declares Void as return type. Use empty list instead.", method);

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
			result[i] = TypeConversionRegistry.toLua(result[i]);

		if (validateReturn) {
			Preconditions.checkArgument(result.length == returnTypes.length, "Returning invalid number of values from method %s, expected %s, got %s", method, returnTypes.length, result.length);
			for (int i = 0; i < result.length; i++) {
				LuaType expected = returnTypes[i];
				Object got = result[i];
				Preconditions.checkArgument(got == null || ReflectionHelper.compareTypes(expected.getJavaType(), got.getClass()), "Invalid type of return value %s: expected %s, got %s", i, expected, got);
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
			int argIndex = 0;
			try {
				for (ConvertedArgument arg : luaArgs) {
					if (arg.isVarArg) {
						int varargSize = Math.max(0, luaValues.length - argIndex);
						Object vararg = Array.newInstance(arg.javaType, varargSize);

						for (int i = 0; i < varargSize; i++) {
							Object value = luaValues[argIndex++];
							Preconditions.checkArgument(arg.isNullable || value != null, "Vararg parameter '%s' has null value, but is not marked as nullable", arg.name);
							Object converted = arg.convert(value);
							Array.set(vararg, i, converted);
						}

						setArg(arg.javaArgIndex, vararg);
					} else {
						int i = argIndex++;
						if (i >= luaValues.length) {
							Preconditions.checkState(arg.isOptional, "Parameter '%s' is missing", arg.name);
							setArg(arg.javaArgIndex, null);
						} else {
							Object value = luaValues[i];
							Preconditions.checkArgument(arg.isNullable || value != null, "Parameter '%s' has null value, but is not marked as nullable", arg.name);
							setArg(arg.javaArgIndex, arg.convert(value));
						}
					}
				}
				Preconditions.checkState(argIndex >= luaValues.length, "Not all lua values used, last index: %s", argIndex - 1);
			} catch (ArrayIndexOutOfBoundsException e) {
				Log.log(Level.FINE, e, "Trying to access arg index, args = %s", Arrays.toString(luaValues));
				throw new IllegalArgumentException(String.format("Invalid Lua parameter count, needs %s, got %s", luaArgs.size(), luaValues.length));
			}

			return this;
		}

		@Override
		public Object[] call() throws Exception {
			for (int i = 0; i < args.length; i++)
				Preconditions.checkState(isSet.contains(i), "Parameter %s value not set", i);

			Object result = method.invoke(target, args);

			if (result instanceof IMultiReturn) return validateResult(((IMultiReturn)result).getObjects());
			else if (result == null) return validateResult();
			else return validateResult(result);
		}
	}

	public CallWrap createWrapper(Object target) {
		return new CallWrap(target);
	}

	public void nameJavaArg(int index, String name) {
		Preconditions.checkArgument(index < javaArgs.size(),
				"Can't assign name '%s' to argument %s in method '%s'. Possible missing argument or @Freeform?",
				name, index, method);
		Integer prev = namedArgs.put(name, index);
		Preconditions.checkArgument(prev == null || prev == index, "Trying to replace '%s' mapping from  %s, got %s", name, prev, index);
	}

	public void checkJavaArgNames(String... allowedNames) {
		Set<String> allowed = ImmutableSet.copyOf(allowedNames);
		Set<String> unknown = Sets.difference(namedArgs.keySet(), allowed);
		Preconditions.checkState(unknown.isEmpty(), "Unknown named arg(s) %s in method '%s'. Allowed args: %s", unknown, method, allowed);
	}

	public void checkJavaArgType(String name, Class<?> cls) {
		Integer index = namedArgs.get(name);
		if (index != null) {
			final Class<?> expected = javaArgs.get(index);
			Preconditions.checkArgument(expected.isAssignableFrom(cls), "Invalid argument type in method %s, was %s, got %s", method, expected, cls);
		}
	}

	public void validate() {
		Set<Integer> needed = Sets.newHashSet();
		for (int i = 0; i < javaArgs.size(); i++)
			needed.add(i);

		Set<Integer> named = Sets.newHashSet(namedArgs.values());
		Set<Integer> missing = Sets.difference(needed, named);
		Preconditions.checkState(missing.isEmpty(), "Arguments %s from method %s are not named", missing, method);

		Set<Integer> extra = Sets.difference(named, needed);
		Preconditions.checkState(missing.isEmpty(), "Lua arguments %s from method %s are named", extra, method);
	}

	public Map<String, Object> describe() {
		Map<String, Object> result = Maps.newHashMap();
		result.put("name", name);
		result.put("description", description);

		{
			Map<Integer, String> tmp = Maps.newHashMap();
			int i = 1;
			for (LuaType t : returnTypes)
				tmp.put(i++, t.toString());
			result.put("returnTypes", tmp);
		}

		{
			Map<Integer, Object> tmp = Maps.newHashMap();
			int i = 1;
			for (ConvertedArgument arg : luaArgs)
				tmp.put(i++, arg.describe());
			result.put("args", tmp);
		}
		return result;
	}

	public String signature() {
		return name + "(" + Joiner.on(",").join(luaArgs) + ")";
	}
}
