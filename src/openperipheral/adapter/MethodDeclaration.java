package openperipheral.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import openmods.Log;
import openperipheral.TypeConversionRegistry;
import openperipheral.api.*;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class MethodDeclaration {

	public static class ConvertedArgument {
		public final String name;
		public final String description;
		public final LuaType luaType;
		public final Class<?> javaClass;
		public final boolean isVarArg;
		private final int javaArgIndex;

		private ConvertedArgument(Arg arg, Class<?> javaClass, int javaArgIndex, boolean isVarArg) {
			this.name = arg.name();
			this.description = arg.description();
			this.luaType = arg.type();
			if (isVarArg) this.javaClass = javaClass.getComponentType();
			else this.javaClass = javaClass;
			this.isVarArg = isVarArg;
			this.javaArgIndex = javaArgIndex;
		}

		public Object convert(Object o) {
			if (o == null) return null;
			return TypeConversionRegistry.fromLua(o, javaClass);
		}

		public Map<String, Object> describe() {
			Map<String, Object> result = Maps.newHashMap();
			result.put("type", luaType.toString());
			result.put("name", name);
			result.put("description", description);
			result.put("vararg", isVarArg);
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

	public MethodDeclaration(Method method, LuaMethod luaMethod) {
		this.method = method;

		String luaName = luaMethod.name();
		this.name = (LuaMethod.USE_METHOD_NAME.equals(luaName))? method.getName() : luaName;
		this.description = luaMethod.description();
		this.returnTypes = new LuaType[] { luaMethod.returnType() };
		this.validateReturn = false;

		Class<?> methodArgs[] = method.getParameterTypes();

		final Arg declaredLuaArgs[] = luaMethod.args();

		final int luaArgsStart = methodArgs.length - declaredLuaArgs.length;
		Preconditions.checkArgument(luaArgsStart >= 0, "Method %s has less arguments than declared", method);

		ImmutableList.Builder<ConvertedArgument> luaArgs = ImmutableList.builder();

		for (int arg = 0; arg < declaredLuaArgs.length; arg++) {
			int javaArgIndex = luaArgsStart + arg;
			luaArgs.add(new ConvertedArgument(declaredLuaArgs[arg], methodArgs[javaArgIndex], javaArgIndex, false));
		}

		this.luaArgs = luaArgs.build();
		this.javaArgs = ImmutableList.copyOf(Arrays.copyOf(methodArgs, luaArgsStart));
	}

	public MethodDeclaration(Method method, LuaCallable meta) {
		this.method = method;

		String luaName = meta.name();
		this.name = (LuaCallable.USE_METHOD_NAME.equals(luaName))? method.getName() : luaName;
		this.description = meta.description();
		this.returnTypes = meta.returnTypes();
		this.validateReturn = meta.validateReturn();

		Class<?> methodArgs[] = method.getParameterTypes();
		Annotation[][] argAnnotations = method.getParameterAnnotations();

		ImmutableList.Builder<ConvertedArgument> luaArgs = ImmutableList.builder();
		ImmutableList.Builder<Class<?>> javaArgs = ImmutableList.builder();
		boolean isInLuaArgs = false;
		boolean isVarArg = method.isVarArgs();

		for (int i = 0; i < methodArgs.length; i++) {
			boolean isLastArg = i == (methodArgs.length - 1);
			final Class<?> cls = methodArgs[i];
			Map<Class<? extends Annotation>, Annotation> annotations = Maps.newIdentityHashMap();

			for (Annotation a : argAnnotations[i])
				annotations.put(a.annotationType(), a);

			boolean isLuaArg = false;

			Annotation tmp = annotations.get(Arg.class);
			if (tmp != null) {
				luaArgs.add(new ConvertedArgument((Arg)tmp, cls, i, isLastArg && isVarArg));
				isLuaArg = true;
				isInLuaArgs = true;
			}

			Preconditions.checkState(!isInLuaArgs || isLuaArg, "Argument %s in method %s look like Java arg, but is in Lua part (perhaps missing Arg annotation?)", i, method);

			tmp = annotations.get(Named.class);
			if (tmp != null) {
				Preconditions.checkState(!isInLuaArgs, "Argument %s in method %s is Lua arg, but has Named annotation", i, method);
				namedArgs.put(((Named)tmp).value(), i);
			}

			if (!isLuaArg) javaArgs.add(cls);
		}

		this.luaArgs = luaArgs.build();
		this.javaArgs = javaArgs.build();
	}

	private Object[] validateResult(Object... result) {
		for (int i = 0; i < result.length; i++)
			result[i] = TypeConversionRegistry.toLua(result[i]);

		if (validateReturn) {
			Preconditions.checkArgument(result.length == returnTypes.length, "Returning invalid number of values from method %s, expected %s, got %s", method, returnTypes.length, result.length);
			for (int i = 0; i < result.length; i++) {
				LuaType expected = returnTypes[i];
				Object got = result[i];
				Preconditions.checkArgument(expected.getJavaType().isInstance(got), "Invalid type of return value %s: expected %s, got %s", i, expected, got);
			}
		}

		return result;
	}

	public class CallWrap implements Callable<Object[]> {
		private final Object[] args = new Object[javaArgs.size() + luaArgs.size()];
		private final Object target;

		public CallWrap(Object target) {
			this.target = target;
		}

		private CallWrap setArg(int position, Object value) {
			Preconditions.checkState(args[position] == null, "Trying to set already defined argument %s in method %s", position, method);
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
						int varargSize = luaValues.length - argIndex;
						Object vararg = Array.newInstance(arg.javaClass, varargSize);

						for (int i = 0; i < varargSize; i++) {
							Object value = luaValues[argIndex++];
							Object converted = arg.convert(value);
							Array.set(vararg, i, converted);
						}

						args[arg.javaArgIndex] = vararg;
					} else {
						Object value = luaValues[argIndex++];
						args[arg.javaArgIndex] = arg.convert(value);
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				Log.log(Level.FINE, e, "Trying to access arg index, args = %s", Arrays.toString(luaValues));
				throw new IllegalArgumentException(String.format("Invalid Lua parameter count, needs %s, got %s", luaArgs.size(), luaValues.length));
			}

			return this;
		}

		@Override
		public Object[] call() throws Exception {
			for (int i = 0; i < args.length; i++)
				Preconditions.checkNotNull(args[i], "Parameter %s value not set", i);

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
		Preconditions.checkElementIndex(index, javaArgs.size(), "java argument index");
		Integer prev = namedArgs.put(name, index);
		Preconditions.checkArgument(prev == null || prev == index, "Trying to replace '%s' mapping from  %s, got %s", name, prev, index);
	}

	public void checkJavaArgType(String name, Class<?> cls) {
		Integer index = namedArgs.get(name);
		if (index != null) {
			final Class<?> expected = javaArgs.get(index);
			Preconditions.checkArgument(expected.isAssignableFrom(cls), "Invalid argument type, was %s, got %s", expected, cls);
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
