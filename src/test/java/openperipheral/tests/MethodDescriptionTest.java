package openperipheral.tests;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import openperipheral.adapter.DefaultAttributeProperty;
import openperipheral.adapter.IAttributeProperty;
import openperipheral.adapter.method.ArgVisitor;
import openperipheral.adapter.method.ArgWrapper;
import openperipheral.adapter.method.Argument;
import openperipheral.adapter.method.ArgumentDefinitionException;
import openperipheral.api.adapter.method.Arg;
import openperipheral.api.adapter.method.Env;
import openperipheral.api.adapter.method.Optionals;
import openperipheral.api.adapter.method.ScriptCallable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class MethodDescriptionTest {

	private static class TargetA {}

	private static class TargetB extends TargetA {}

	private static Method getMethod(Class<?> cls) {
		for (Method m : cls.getMethods())
			if (m.getName().equals("test")) return m;

		throw new AssertionError();
	}

	private final List<TestableArgVisitor> allTests = Lists.newArrayList();

	@After
	public void ensureAllDone() {
		for (TestableArgVisitor v : allTests)
			Assert.assertTrue(v.isDone());

		allTests.clear();
	}

	private static class TestableArgVisitor extends ArgVisitor {
		private final Map<Integer, Argument> luaArgs = Maps.newHashMap();

		private final Map<Integer, Class<?>> unnamedArg = Maps.newHashMap();

		private final Map<Integer, Class<?>> envArgs = Maps.newHashMap();

		private final Set<Integer> checkedArgs = Sets.newHashSet();

		private boolean done;

		@Override
		protected void visitScriptArg(int argIndex, Argument arg) {
			final Argument prev = luaArgs.put(argIndex, arg);
			Assert.assertNull(prev);
		}

		@Override
		protected void visitUnnamedArg(int argIndex, TypeToken<?> type) {
			final Class<?> prev = unnamedArg.put(argIndex, type.getRawType());
			Assert.assertNull(prev);
		}

		@Override
		protected void visitEnvArg(int argIndex, TypeToken<?> type) {
			final Class<?> prev = envArgs.put(argIndex, type.getRawType());
			Assert.assertNull(prev);
		}

		public TestableArgVisitor checkLuaArg(int index, String name, String desc, String type, DefaultAttributeProperty... props) {
			final Argument arg = luaArgs.get(index);
			Assert.assertNotNull(arg);

			Assert.assertEquals(name, arg.name());
			Assert.assertEquals(desc, arg.description());

			for (IAttributeProperty prop : props)
				Assert.assertTrue(arg.is(prop));

			final Collection<? extends IAttributeProperty> notProps;
			if (props.length == 0)
				notProps = EnumSet.allOf(DefaultAttributeProperty.class);
			else
				notProps = EnumSet.complementOf(EnumSet.copyOf(Arrays.asList(props)));

			for (IAttributeProperty prop : notProps)
				Assert.assertFalse(arg.is(prop));

			Assert.assertEquals(type, arg.type().describe());

			checkedArgs.add(index);
			return this;
		}

		public TestableArgVisitor checkEnvArg(int index, Class<?> cls) {
			Assert.assertEquals(cls, envArgs.get(index));
			checkedArgs.add(index);
			return this;
		}

		public TestableArgVisitor checkUnnamedArg(int index, Class<?> cls) {
			Assert.assertEquals(cls, unnamedArg.get(index));
			checkedArgs.add(index);
			return this;
		}

		public void done() {
			Set<Integer> allArgs = Sets.newHashSet();

			for (Integer i : luaArgs.keySet())
				Assert.assertTrue(allArgs.add(i));

			for (Integer i : envArgs.keySet())
				Assert.assertTrue(allArgs.add(i));

			for (Integer i : unnamedArg.keySet())
				Assert.assertTrue(allArgs.add(i));

			Assert.assertEquals(allArgs, checkedArgs);

			this.done = true;
		}

		public boolean isDone() {
			return this.done;
		}
	}

	protected TestableArgVisitor wrapMethod(Class<?> cls) {
		final Method m = getMethod(cls);
		return wrapMethod(cls, m);
	}

	protected TestableArgVisitor wrapMethod(Class<?> cls, Method method) {
		final Method m = getMethod(cls);
		final List<ArgWrapper> args = ArgWrapper.fromMethod(cls, m);
		final TestableArgVisitor testableArgVisitor = new TestableArgVisitor();
		testableArgVisitor.visitArgs(args, m.isVarArgs());
		allTests.add(testableArgVisitor);
		return testableArgVisitor;
	}

	protected TestableArgVisitor wrapMethodFail(Class<?> cls) {
		final Method m = getMethod(cls);
		return wrapMethod(cls, m);
	}

	protected void wrapMethodFail(Class<?> cls, Method method) {
		final Method m = getMethod(cls);
		final List<ArgWrapper> args = ArgWrapper.fromMethod(cls, m);
		new TestableArgVisitor().visitArgs(args, m.isVarArgs());
	}

	@Test
	public void testSingleLuaOnly() {
		class SingleLuaOnly {
			@ScriptCallable
			public String test(@Arg(name = "b", description = "test") int a) {
				return "" + a;
			}
		}

		wrapMethod(SingleLuaOnly.class)
				.checkLuaArg(0, "b", "test", "number")
				.done();
	}

	@Test
	public void testSingleOptionalLuaOnly() {
		class SingleOptionalLuaOnly {
			@ScriptCallable
			public String test(@Optionals @Arg(name = "a") Integer a) {
				return "" + a;
			}
		}

		wrapMethod(SingleOptionalLuaOnly.class)
				.checkLuaArg(0, "a", "", "number", DefaultAttributeProperty.OPTIONAL)
				.done();
	}

	@Test
	public void testVarargLuaStart() {
		class VarargLuaStart {
			@ScriptCallable
			public String test(@Arg(name = "a") int... a) {
				return Arrays.toString(a);
			}
		}

		wrapMethod(VarargLuaStart.class)
				.checkLuaArg(0, "a", "", "number", DefaultAttributeProperty.VARIADIC)
				.done();
	}

	@Test
	public void testSingleOptional() {
		class SingleOptional {
			@ScriptCallable
			public String test(TargetB target, @Optionals @Arg(name = "a") String b) {
				return "A";
			}
		}

		wrapMethod(SingleOptional.class)
				.checkUnnamedArg(0, TargetB.class)
				.checkLuaArg(1, "a", "", "string", DefaultAttributeProperty.OPTIONAL)
				.done();
	}

	@Test
	public void testDoubleOptionals() {
		class DoubleOptionals {
			@ScriptCallable
			public String test(TargetB target, @Optionals @Arg(name = "a") String a, @Arg(name = "b") Double b) {
				return "A";
			}
		}

		wrapMethod(DoubleOptionals.class)
				.checkUnnamedArg(0, TargetB.class)
				.checkLuaArg(1, "a", "", "string", DefaultAttributeProperty.OPTIONAL)
				.checkLuaArg(2, "b", "", "number", DefaultAttributeProperty.OPTIONAL)
				.done();
	}

	@Test
	public void testFullOptional() {
		class NormalBeforeOptional {
			@ScriptCallable
			public String test(TargetB target, @Arg(name = "a") int a, @Optionals @Arg(name = "b") String b) {
				return "A";
			}
		}

		wrapMethod(NormalBeforeOptional.class)
				.checkUnnamedArg(0, TargetB.class)
				.checkLuaArg(1, "a", "", "number")
				.checkLuaArg(2, "b", "", "string", DefaultAttributeProperty.OPTIONAL)
				.done();
	}

	@Test
	public void testVararg() {
		class Vararg {
			@ScriptCallable
			public String test(TargetB target, @Arg(name = "a") int... a) {
				return Arrays.toString(a);
			}
		}

		wrapMethod(Vararg.class)
				.checkUnnamedArg(0, TargetB.class)
				.checkLuaArg(1, "a", "", "number", DefaultAttributeProperty.VARIADIC)
				.done();
	}

	@Test
	public void testNonVarargArray() {
		class NonVarargArray {
			@ScriptCallable
			public String test(TargetB target, @Arg(name = "a") int[] a) {
				return Arrays.toString(a);
			}
		}

		wrapMethod(NonVarargArray.class)
				.checkUnnamedArg(0, TargetB.class)
				.checkLuaArg(1, "a", "", "[number]")
				.done();
	}

	@Test
	public void testOptionalVararg() {
		class OptionalVararg {
			@ScriptCallable
			public String test(@Optionals @Arg(name = "a") Integer... a) {
				return Arrays.toString(a);
			}
		}

		wrapMethod(OptionalVararg.class)
				.checkLuaArg(0, "a", "", "number", DefaultAttributeProperty.VARIADIC)
				.done();
	}

	private interface EnvA {}

	private interface EnvB {}

	@Test
	public void testSingleEnv() {
		class SingleEnv {
			@ScriptCallable
			public String test(@Env EnvA access) {
				return access.toString();
			}
		}

		wrapMethod(SingleEnv.class)
				.checkEnvArg(0, EnvA.class)
				.done();
	}

	@Test
	public void testDoubleEnv() {
		class DoubleEnv {
			@ScriptCallable
			public String test(@Env EnvA access, @Env EnvB accessB) {
				return access.toString();
			}
		}

		wrapMethod(DoubleEnv.class)
				.checkEnvArg(0, EnvA.class)
				.checkEnvArg(1, EnvB.class)
				.done();
	}

	@Test
	public void testTargetEnv() {
		class TargetEnv {
			@ScriptCallable
			public String test(TargetB target, @Env EnvA access) {
				return access.toString();
			}
		}

		wrapMethod(TargetEnv.class)
				.checkUnnamedArg(0, TargetB.class)
				.checkEnvArg(1, EnvA.class)
				.done();
	}

	@Test
	public void testEnvLua() {
		class TargetEnvLua {
			@ScriptCallable
			public String test(TargetB target, @Env EnvA access, @Arg(name = "z") Integer a) {
				return access.toString();
			}
		}

		wrapMethod(TargetEnvLua.class)
				.checkUnnamedArg(0, TargetB.class)
				.checkEnvArg(1, EnvA.class)
				.checkLuaArg(2, "z", "", "number")
				.done();
	}

	@Test
	public void testEverything() {
		class Everything {
			@ScriptCallable
			public String test(TargetB target, @Env EnvA access, @Arg(name = "a") int a, @Optionals @Arg(name = "b") String b, @Arg(name = "var") Integer... v) {
				return "test";
			}
		}

		wrapMethod(Everything.class)
				.checkUnnamedArg(0, TargetB.class)
				.checkEnvArg(1, EnvA.class)
				.checkLuaArg(2, "a", "", "number")
				.checkLuaArg(3, "b", "", "string", DefaultAttributeProperty.OPTIONAL)
				.checkLuaArg(4, "var", "", "number", DefaultAttributeProperty.VARIADIC)
				.done();
	}

	@Test
	public void testGeneric() {
		class GenericBase<T, E, P> {
			@ScriptCallable
			public String test(T target, @Env E access, @Arg(name = "a") P a) {
				return String.valueOf(a);
			}
		}

		class GenericDerrived extends GenericBase<TargetB, EnvA, Float> {}

		wrapMethod(GenericDerrived.class)
				.checkUnnamedArg(0, TargetB.class)
				.checkEnvArg(1, EnvA.class)
				.checkLuaArg(2, "a", "", "number")
				.done();
	}

	@Test
	public void testNullableLuaArg() {
		class TargetEnv {
			@ScriptCallable
			public String test(TargetB target, @Arg(name = "a", nullable = true) Boolean a) {
				return a.toString();
			}
		}

		wrapMethod(TargetEnv.class)
				.checkUnnamedArg(0, TargetB.class)
				.checkLuaArg(1, "a", "", "boolean", DefaultAttributeProperty.NULLABLE)
				.done();
	}

	@Test
	public void testNullableLuaVarArg() {
		class TargetEnv {
			@ScriptCallable
			public String test(TargetB target, @Arg(name = "a", nullable = true) Boolean... a) {
				return a.toString();
			}
		}

		wrapMethod(TargetEnv.class)
				.checkUnnamedArg(0, TargetB.class)
				.checkLuaArg(1, "a", "", "boolean", DefaultAttributeProperty.NULLABLE, DefaultAttributeProperty.VARIADIC)
				.done();
	}

	@Test(expected = IllegalStateException.class)
	public void testOptionalNullable() {
		class TargetEnv {
			@ScriptCallable
			public String test(TargetB target, @Optionals @Arg(name = "a", nullable = true) Boolean a) {
				return a.toString();
			}
		}

		wrapMethod(TargetEnv.class)
				.checkUnnamedArg(0, TargetB.class)
				.checkLuaArg(1, "a", "", "boolean", DefaultAttributeProperty.NULLABLE)
				.done();
	}

	@Test
	public void testTwoUnnamed() {
		class TwoUnnamed {
			@ScriptCallable
			public boolean test(TargetB target, TargetA target2) {
				return true;
			}
		}

		wrapMethod(TwoUnnamed.class)
				.checkUnnamedArg(0, TargetB.class)
				.checkUnnamedArg(1, TargetA.class)
				.done();
	}

	@Test(expected = ArgumentDefinitionException.class)
	public void testUnnamedAfterLua() {
		class UnnamedAfterLua {
			@ScriptCallable
			public boolean test(@Arg(name = "foo") String arg, TargetB target) {
				return true;
			}
		}

		wrapMethodFail(UnnamedAfterLua.class);
	}

	@Test(expected = ArgumentDefinitionException.class)
	public void testEnvAfterLua() {
		class EnvAfterLua {
			@ScriptCallable
			public boolean test(@Arg(name = "foo") String arg, @Env EnvA target) {
				return true;
			}
		}

		wrapMethodFail(EnvAfterLua.class);
	}

	@Test(expected = ArgumentDefinitionException.class)
	public void testUnamedAfterEnv() {
		class UnamedAfterEnv {
			@ScriptCallable
			public boolean test(@Env EnvA e1, EnvB target) {
				return true;
			}
		}

		wrapMethodFail(UnamedAfterEnv.class);
	}

	@Test(expected = ArgumentDefinitionException.class)
	public void testOptionalUnnnamed() {
		class OptionalUnnnamed {
			@ScriptCallable
			public boolean test(@Optionals EnvA target) {
				return true;
			}
		}

		wrapMethodFail(OptionalUnnnamed.class);
	}

	@Test(expected = ArgumentDefinitionException.class)
	public void testOptionalEnv() {
		class OptionalEnv {
			@ScriptCallable
			public boolean test(@Optionals @Env EnvA target) {
				return true;
			}
		}

		wrapMethodFail(OptionalEnv.class);
	}

}
