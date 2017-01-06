package openperipheral.tests;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;
import openperipheral.adapter.IMethodCaller;
import openperipheral.adapter.method.MethodWrapperBuilder;
import openperipheral.api.adapter.method.Alias;
import openperipheral.api.adapter.method.Arg;
import openperipheral.api.adapter.method.Env;
import openperipheral.api.adapter.method.ReturnType;
import openperipheral.api.adapter.method.ScriptCallable;
import openperipheral.api.converter.IConverter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class MethodCallerTest {

	private static final Set<Class<?>> NO_OPTIONALS = ImmutableSet.of();

	private static final Set<Class<?>> CONVERTER_ONLY = ImmutableSet.<Class<?>> of(IConverter.class);

	private static class TargetA {}

	private static class TargetB extends TargetA {}

	private static class EnvA {}

	private static class EnvB {}

	private static final Set<Class<?>> REQUIRE_ENV_A = ImmutableSet.<Class<?>> of(EnvA.class);

	private static final Set<Class<?>> REQUIRE_ENV_A_B = ImmutableSet.<Class<?>> of(EnvA.class, EnvB.class);

	private static final Set<Class<?>> REQUIRE_ENV_A_CONVERTER = ImmutableSet.<Class<?>> of(EnvA.class, IConverter.class);

	private static Method getMethod(Class<?> cls) {
		for (Method m : cls.getMethods())
			if (m.getName().equals("test")) return m;

		throw new IllegalArgumentException();
	}

	private static IMethodCaller checkBoundMethodCaller(Set<Class<?>> envs, Object target, MethodWrapperBuilder decl) {
		decl.defineTargetArg(0, TargetB.class);
		final IMethodCaller caller = decl.createBoundMethodCaller(target);
		Assert.assertEquals(envs, caller.requiredEnvArgs());
		return caller;
	}

	private static IMethodCaller checkUnboundMethodCaller(Set<Class<?>> envs, MethodWrapperBuilder decl) {
		final IMethodCaller caller = decl.createUnboundMethodCaller();
		Assert.assertEquals(envs, caller.requiredEnvArgs());
		return caller;
	}

	private static Object[] wrap(Object... objs) {
		return objs;
	}

	private static MethodWrapperBuilder createMethodDecl(Class<?> cls) {
		final Method m = getMethod(cls);
		return new MethodWrapperBuilder(cls, m, m.getAnnotation(ScriptCallable.class), "test");
	}

	public interface UnboundMethodVoidReturn {
		@ScriptCallable(returnTypes = {})
		public void test(TargetA target);
	}

	@Test
	public void testUnboundMethodNoArgsVoidReturn() throws Exception {
		MethodWrapperBuilder decl = createMethodDecl(UnboundMethodVoidReturn.class);
		final UnboundMethodVoidReturn mock = Mockito.mock(UnboundMethodVoidReturn.class);
		final IMethodCaller caller = checkBoundMethodCaller(NO_OPTIONALS, mock, decl);
		final TargetB target = new TargetB();

		final Object[] result = caller.startCall(target).call();
		Assert.assertArrayEquals(wrap(), result);

		Mockito.verify(mock).test(target);
	}

	public interface BoundMethodVoidReturn {
		@ScriptCallable(returnTypes = {})
		public void test();
	}

	@Test
	public void testBoundMethodNoArgsVoidReturn() throws Exception {
		MethodWrapperBuilder decl = createMethodDecl(BoundMethodVoidReturn.class);
		final BoundMethodVoidReturn mock = Mockito.mock(BoundMethodVoidReturn.class);
		final IMethodCaller caller = checkUnboundMethodCaller(NO_OPTIONALS, decl);

		final Object[] result = caller.startCall(mock).call();
		Assert.assertArrayEquals(wrap(), result);

		Mockito.verify(mock).test();
	}

	public interface UnboundMethod {
		@ScriptCallable(returnTypes = ReturnType.NUMBER)
		public int test(TargetA target);
	}

	@Test
	public void testUnboundMethodNoArgsDescription() {
		MethodWrapperBuilder decl = createMethodDecl(UnboundMethod.class);
		Assert.assertEquals(Lists.newArrayList(), decl.getMethodDescription().arguments());
		Assert.assertEquals("number", decl.getMethodDescription().returnTypes().describe());
	}

	@Test
	public void testUnboundMethodNoArgs() throws Exception {
		MethodWrapperBuilder decl = createMethodDecl(UnboundMethod.class);
		final UnboundMethod mock = Mockito.mock(UnboundMethod.class);
		final IMethodCaller caller = checkBoundMethodCaller(CONVERTER_ONLY, mock, decl);
		final TargetB target = new TargetB();
		Mockito.when(mock.test(Matchers.any(TargetA.class))).thenReturn(94);

		final IConverter converter = Mockito.mock(IConverter.class);
		Mockito.when(converter.fromJava(Matchers.any(Integer.class))).thenReturn(65.4);

		final Object[] result = caller.startCall(target).setEnv(IConverter.class, converter).call();

		Mockito.verify(mock).test(target);
		Mockito.verify(converter).fromJava(94);
		Assert.assertArrayEquals(wrap(65.4), result);
	}

	public interface BoundMethod {
		@ScriptCallable(returnTypes = ReturnType.NUMBER)
		public int test();
	}

	@Test
	public void testBoundMethodNoArgs() throws Exception {
		MethodWrapperBuilder decl = createMethodDecl(BoundMethod.class);
		final BoundMethod mock = Mockito.mock(BoundMethod.class);
		final IMethodCaller caller = checkUnboundMethodCaller(CONVERTER_ONLY, decl);

		final IConverter converter = Mockito.mock(IConverter.class);
		Mockito.when(mock.test()).thenReturn(432);
		Mockito.when(converter.fromJava(Matchers.any(Integer.class))).thenReturn(54.2);

		final Object[] result = caller.startCall(mock).setEnv(IConverter.class, converter).call();

		Mockito.verify(mock).test();
		Mockito.verify(converter).fromJava(432);
		Assert.assertArrayEquals(wrap(54.2), result);
	}

	public interface UnboundMethodWithSingleLuaArg {
		@ScriptCallable(returnTypes = ReturnType.STRING)
		public String test(TargetA target, @Arg(name = "a") String a);
	}

	@Test
	public void testUnboundMethodWithSingleLuaArg() throws Exception {
		MethodWrapperBuilder decl = createMethodDecl(UnboundMethodWithSingleLuaArg.class);

		final UnboundMethodWithSingleLuaArg mock = Mockito.mock(UnboundMethodWithSingleLuaArg.class);
		final IMethodCaller caller = checkBoundMethodCaller(CONVERTER_ONLY, mock, decl);
		final TargetB target = new TargetB();

		final IConverter converter = Mockito.mock(IConverter.class);
		Mockito.when(converter.toJava(Matchers.any(), Matchers.<Type> any())).thenReturn("hello");
		Mockito.when(mock.test(Matchers.any(TargetA.class), Matchers.anyString())).thenReturn("hi");
		Mockito.when(converter.fromJava(Matchers.any(Integer.class))).thenReturn("world");

		final Object[] result = caller.startCall(target).setEnv(IConverter.class, converter).call(123);

		Mockito.verify(converter).toJava(123, (Type)String.class);
		Mockito.verify(mock).test(target, "hello");
		Assert.assertArrayEquals(wrap("world"), result);
	}

	public interface BoundMethodWithSingleLuaArg {
		@ScriptCallable(returnTypes = ReturnType.STRING)
		public String test(TargetA target, @Arg(name = "a") Number a);
	}

	@Test
	public void testBoundMethodWithSingleLuaArg() throws Exception {
		MethodWrapperBuilder decl = createMethodDecl(BoundMethodWithSingleLuaArg.class);

		final BoundMethodWithSingleLuaArg mock = Mockito.mock(BoundMethodWithSingleLuaArg.class);
		final IMethodCaller caller = checkBoundMethodCaller(CONVERTER_ONLY, mock, decl);
		final TargetB target = new TargetB();

		final IConverter converter = Mockito.mock(IConverter.class);
		Mockito.when(converter.toJava(Matchers.any(), Matchers.<Type> any())).thenReturn(123);
		Mockito.when(mock.test(Matchers.any(TargetA.class), Matchers.any(Number.class))).thenReturn("java");
		Mockito.when(converter.fromJava(Matchers.any())).thenReturn("lua");

		final Object[] result = caller.startCall(target).setEnv(IConverter.class, converter).call(321);

		Mockito.verify(converter).toJava(321, (Type)Number.class);
		Mockito.verify(mock).test(target, 123);
		Mockito.verify(converter).fromJava("java");
		Assert.assertArrayEquals(wrap("lua"), result);
	}

	public interface UnboundMethodWithSingleEnvArg {
		@ScriptCallable(returnTypes = {})
		public void test(TargetA target, @Env EnvA env);
	}

	@Test
	public void UnboundMethodWithSingleEnvArg() throws Exception {
		MethodWrapperBuilder decl = createMethodDecl(UnboundMethodWithSingleEnvArg.class);

		final UnboundMethodWithSingleEnvArg mock = Mockito.mock(UnboundMethodWithSingleEnvArg.class);
		final IMethodCaller caller = checkBoundMethodCaller(REQUIRE_ENV_A, mock, decl);
		final TargetB target = new TargetB();
		final EnvA env = new EnvA();

		final Object[] result = caller.startCall(target).setEnv(EnvA.class, env).call();

		Mockito.verify(mock).test(target, env);
		Assert.assertArrayEquals(wrap(), result);
	}

	public interface BoundMethodWithSingleEnvArg {
		@ScriptCallable(returnTypes = {})
		public void test(@Env EnvA env);
	}

	@Test
	public void testBoundMethodWithSingleEnvArg() throws Exception {
		MethodWrapperBuilder decl = createMethodDecl(BoundMethodWithSingleEnvArg.class);

		final BoundMethodWithSingleEnvArg mock = Mockito.mock(BoundMethodWithSingleEnvArg.class);
		final IMethodCaller caller = checkUnboundMethodCaller(REQUIRE_ENV_A, decl);
		final EnvA env = new EnvA();

		final Object[] result = caller.startCall(mock).setEnv(EnvA.class, env).call();

		Mockito.verify(mock).test(env);
		Assert.assertArrayEquals(wrap(), result);
	}

	public interface UnboundMethodWithTwoEnvArg {
		@ScriptCallable(returnTypes = {})
		public void test(TargetA target, @Env EnvB envB, @Env EnvA envA);
	}

	@Test
	public void UnboundMethodWithTwoEnvArg() throws Exception {
		MethodWrapperBuilder decl = createMethodDecl(UnboundMethodWithTwoEnvArg.class);

		final UnboundMethodWithTwoEnvArg mock = Mockito.mock(UnboundMethodWithTwoEnvArg.class);
		final IMethodCaller caller = checkBoundMethodCaller(REQUIRE_ENV_A_B, mock, decl);
		final TargetB target = new TargetB();
		final EnvA envA = new EnvA();
		final EnvB envB = new EnvB();

		final Object[] result = caller.startCall(target).setEnv(EnvA.class, envA).setEnv(EnvB.class, envB).call();

		Mockito.verify(mock).test(target, envB, envA);
		Assert.assertArrayEquals(wrap(), result);
	}

	public interface BoundMethodWithTwoEnvArg {
		@ScriptCallable(returnTypes = {})
		public void test(@Env EnvB envB, @Env EnvA env);
	}

	@Test
	public void testBoundMethodWithTwoEnvArg() throws Exception {
		MethodWrapperBuilder decl = createMethodDecl(BoundMethodWithTwoEnvArg.class);

		final BoundMethodWithTwoEnvArg mock = Mockito.mock(BoundMethodWithTwoEnvArg.class);
		final IMethodCaller caller = checkUnboundMethodCaller(REQUIRE_ENV_A_B, decl);
		final EnvA envA = new EnvA();
		final EnvB envB = new EnvB();

		final Object[] result = caller.startCall(mock).setEnv(EnvA.class, envA).setEnv(EnvB.class, envB).call();

		Mockito.verify(mock).test(envB, envA);
		Assert.assertArrayEquals(wrap(), result);
	}

	public static class TargetOnly {
		@Alias({ "aliasA", "aliasB" })
		@ScriptCallable(returnTypes = ReturnType.BOOLEAN)
		public boolean test(TargetB target) {
			return true;
		}
	}

	public interface UnboundMethodWithEnvAndLuaArg {
		@ScriptCallable(returnTypes = ReturnType.STRING)
		public String test(TargetA target, @Env EnvA env, @Arg(name = "a") String a);
	}

	@Test
	public void testUnboundMethodWithEnvAndLuaArg() throws Exception {
		MethodWrapperBuilder decl = createMethodDecl(UnboundMethodWithEnvAndLuaArg.class);

		final UnboundMethodWithEnvAndLuaArg mock = Mockito.mock(UnboundMethodWithEnvAndLuaArg.class);
		final IMethodCaller caller = checkBoundMethodCaller(REQUIRE_ENV_A_CONVERTER, mock, decl);
		final TargetB target = new TargetB();
		final EnvA env = new EnvA();

		final IConverter converter = Mockito.mock(IConverter.class);
		Mockito.when(converter.toJava(Matchers.any(), Matchers.<Type> any())).thenReturn("hello");
		Mockito.when(mock.test(Matchers.any(TargetA.class), Matchers.any(EnvA.class), Matchers.anyString())).thenReturn("hi");
		Mockito.when(converter.fromJava(Matchers.any(Integer.class))).thenReturn("world");

		final Object[] result = caller.startCall(target).setEnv(IConverter.class, converter).setEnv(EnvA.class, env).call(123);

		Mockito.verify(converter).toJava(123, (Type)String.class);
		Mockito.verify(mock).test(target, env, "hello");
		Assert.assertArrayEquals(wrap("world"), result);
	}

	public interface BoundMethodWithEnvAndLuaArg {
		@ScriptCallable(returnTypes = ReturnType.STRING)
		public String test(@Env EnvA env, @Arg(name = "a") Number a);
	}

	@Test
	public void testBoundMethodWithEnvAndLuaArg() throws Exception {
		MethodWrapperBuilder decl = createMethodDecl(BoundMethodWithEnvAndLuaArg.class);

		final BoundMethodWithEnvAndLuaArg mock = Mockito.mock(BoundMethodWithEnvAndLuaArg.class);
		final IMethodCaller caller = checkUnboundMethodCaller(REQUIRE_ENV_A_CONVERTER, decl);
		final EnvA env = new EnvA();

		final IConverter converter = Mockito.mock(IConverter.class);
		Mockito.when(converter.toJava(Matchers.any(), Matchers.<Type> any())).thenReturn(123);
		Mockito.when(mock.test(Matchers.any(EnvA.class), Matchers.any(Number.class))).thenReturn("java");
		Mockito.when(converter.fromJava(Matchers.any())).thenReturn("lua");

		final Object[] result = caller.startCall(mock).setEnv(IConverter.class, converter).setEnv(EnvA.class, env).call(321);

		Mockito.verify(converter).toJava(321, (Type)Number.class);
		Mockito.verify(mock).test(env, 123);
		Mockito.verify(converter).fromJava("java");
		Assert.assertArrayEquals(wrap("lua"), result);
	}

	@Test
	public void testDescription_methodAlias() {
		MethodWrapperBuilder decl = createMethodDecl(TargetOnly.class);
		Assert.assertEquals(Sets.newHashSet("test", "aliasA", "aliasB"), Sets.newHashSet(decl.getMethodDescription().getNames()));
	}

}
