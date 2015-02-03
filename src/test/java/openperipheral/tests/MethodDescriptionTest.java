package openperipheral.tests;

import java.lang.reflect.Method;
import java.util.*;

import openperipheral.adapter.method.MethodDeclaration;
import openperipheral.api.adapter.method.*;
import openperipheral.api.helpers.MultiReturn;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class MethodDescriptionTest {

	private static final ImmutableMap<String, Class<?>> NO_OPTIONALS = ImmutableMap.<String, Class<?>> of();

	public static class A {}

	public static class B extends A {}

	public static class C {}

	public static class D {
		public String test() {
			return "out";
		}
	}

	private static Method getMethod(Class<?> cls) {
		for (Method m : cls.getMethods())
			if (m.getName().equals("test")) return m;

		throw new IllegalArgumentException();
	}

	private static void checkNoArgs(MethodDeclaration decl) {
		decl.validatePositionalArgs();
		decl.validateOptionalArgs(NO_OPTIONALS);
	}

	private static void checkTargetOnly(MethodDeclaration decl) {
		decl.validatePositionalArgs(B.class);
		decl.validateOptionalArgs(NO_OPTIONALS);
	}

	private static MethodDeclaration createMethodDecl(Class<?> cls) {
		Method m = getMethod(cls);

		return new MethodDeclaration(m, m.getAnnotation(LuaCallable.class), "test");
	}

	private static Map<String, Class<?>> singleArg(String name, Class<?> cls) {
		return ImmutableMap.<String, Class<?>> of(name, cls);
	}

	private static Map<String, Class<?>> twoArgs(String name1, Class<?> cls1, String name2, Class<?> cls2) {
		return ImmutableMap.<String, Class<?>> of(name1, cls1, name2, cls2);
	}

	public static class BaseTargetOnly {
		@LuaCallable(returnTypes = LuaReturnType.BOOLEAN)
		public boolean test(A target, @Env("env1") A e) {
			return true;
		}
	}

	@Test
	public void testBaseTargetOnly() {
		MethodDeclaration decl = createMethodDecl(BaseTargetOnly.class);
		decl.validatePositionalArgs(B.class);
		decl.validateOptionalArgs(singleArg("env1", B.class));
	}

	public static class TargetOnly {
		@Alias({ "aliasA", "aliasB" })
		@LuaCallable(returnTypes = LuaReturnType.BOOLEAN)
		public boolean test(B target) {
			return true;
		}
	}

	@Test
	public void testTargetOnly() {
		MethodDeclaration decl = createMethodDecl(TargetOnly.class);
		Assert.assertEquals(Sets.newHashSet(decl.getNames()), Sets.newHashSet("test", "aliasA", "aliasB"));
		checkTargetOnly(decl);
	}

	public static class SingleLuaArg {
		@LuaCallable(returnTypes = LuaReturnType.BOOLEAN)
		public boolean test(B target, @Arg(name = "a") int a) {
			return true;
		}
	}

	@Test
	public void testSingleLuaArg() {
		MethodDeclaration decl = createMethodDecl(SingleLuaArg.class);
		checkTargetOnly(decl);
	}

	public static class SingleEnv {
		@LuaCallable(returnTypes = LuaReturnType.STRING)
		public String test(B target, @Env("env1") D access) {
			return access.test();
		}
	}

	@Test
	public void testSingleEnv() {
		MethodDeclaration decl = createMethodDecl(SingleEnv.class);
		decl.validatePositionalArgs(B.class);
		decl.validateOptionalArgs(singleArg("env1", D.class));
	}

	@Test(expected = Exception.class)
	public void testMissingEnvName() {
		MethodDeclaration decl = createMethodDecl(SingleEnv.class);
		decl.validatePositionalArgs(B.class);
		decl.validateOptionalArgs(singleArg("env2", D.class));
	}

	@Test(expected = Exception.class)
	public void testMissingEnvType() {
		MethodDeclaration decl = createMethodDecl(SingleEnv.class);
		decl.validatePositionalArgs(B.class);
		decl.validateOptionalArgs(singleArg("env1", B.class));
	}

	@Test(expected = Exception.class)
	public void testMissingPositioned() {
		MethodDeclaration decl = createMethodDecl(SingleEnv.class);
		decl.validatePositionalArgs(B.class, B.class);
	}

	@Test(expected = Exception.class)
	public void testInvalidTypePositioned() {
		MethodDeclaration decl = createMethodDecl(SingleEnv.class);
		decl.validatePositionalArgs(D.class);
	}

	public static class Empty {
		@LuaCallable(returnTypes = LuaReturnType.STRING)
		public String test() {
			return "oops";
		}
	}

	@Test
	public void testEmpty() {
		MethodDeclaration decl = createMethodDecl(Empty.class);
		checkNoArgs(decl);
	}

	public static class TwoEnvOnly {
		@LuaCallable(returnTypes = LuaReturnType.STRING)
		public String test(@Env("env1") D access, @Env("target") Object target) {
			return access.toString();
		}
	}

	@Test
	public void testTwoEnvOnly() {
		MethodDeclaration decl = createMethodDecl(TwoEnvOnly.class);
		decl.validatePositionalArgs();
		decl.validateOptionalArgs(twoArgs("env1", D.class, "target", Object.class));
	}

	public static class SingleEnvOnly {
		@LuaCallable(returnTypes = LuaReturnType.STRING)
		public String test(@Env("env1") D access) {
			return access.toString();
		}
	}

	@Test
	public void testSingleEnvOnly() {
		MethodDeclaration decl = createMethodDecl(SingleEnvOnly.class);
		decl.validatePositionalArgs();
		decl.validateOptionalArgs(singleArg("env1", D.class));
	}

	public static class SingleLuaOnly {
		@LuaCallable(returnTypes = LuaReturnType.STRING)
		public String test(@Arg(name = "a") int a) {
			return "" + a;
		}
	}

	@Test
	public void testSingleLuaOnly() {
		MethodDeclaration decl = createMethodDecl(SingleLuaOnly.class);
		checkNoArgs(decl);
	}

	public static class SingleOptionalLuaOnly {
		@LuaCallable(returnTypes = LuaReturnType.STRING)
		public String test(@Optionals @Arg(name = "a") Integer a) {
			return "" + a;
		}
	}

	@Test
	public void testSingleOptionalLuaOnly() {
		MethodDeclaration decl = createMethodDecl(SingleOptionalLuaOnly.class);
		checkNoArgs(decl);
	}

	public static class VarargLuaStart {
		@LuaCallable(returnTypes = LuaReturnType.STRING)
		public String test(@Arg(name = "a") int... a) {
			return Arrays.toString(a);
		}
	}

	@Test
	public void testVarargLuaStart() {
		MethodDeclaration decl = createMethodDecl(VarargLuaStart.class);
		checkNoArgs(decl);
	}

	public static class OptionalVararg {
		@LuaCallable(returnTypes = LuaReturnType.STRING)
		public String test(@Optionals @Arg(name = "a") Integer... a) {
			return Arrays.toString(a);
		}
	}

	@Test
	public void testOptionalVararg() {
		MethodDeclaration decl = createMethodDecl(OptionalVararg.class);
		checkNoArgs(decl);
	}

	public static class EnvLua {
		@LuaCallable(returnTypes = LuaReturnType.STRING)
		public String test(B target, @Env("env1") D access, @Arg(name = "a") Integer a) {
			return access.toString();
		}
	}

	@Test
	public void testEnvLua() {
		MethodDeclaration decl = createMethodDecl(EnvLua.class);
		decl.validatePositionalArgs(B.class);
		decl.validateOptionalArgs(singleArg("env1", D.class));
	}

	public static class FullOptional {
		@LuaCallable(returnTypes = LuaReturnType.STRING)
		public String test(B target, @Arg(name = "a") int a, @Optionals @Arg(name = "b") String b) {
			return "A";
		}
	}

	@Test
	public void testFullOptional() {
		MethodDeclaration decl = createMethodDecl(FullOptional.class);
		checkTargetOnly(decl);
	}

	public static class SingleOptional {
		@LuaCallable(returnTypes = LuaReturnType.STRING)
		public String test(B target, @Optionals @Arg(name = "a") String b) {
			return "A";
		}
	}

	@Test
	public void testSingleOptional() {
		MethodDeclaration decl = createMethodDecl(SingleOptional.class);
		checkTargetOnly(decl);
	}

	public static class Vararg {
		@LuaCallable(returnTypes = LuaReturnType.STRING)
		public String test(B target, @Arg(name = "a") int... a) {
			return Arrays.toString(a);
		}
	}

	@Test
	public void testVararg() {
		MethodDeclaration decl = createMethodDecl(Vararg.class);
		checkTargetOnly(decl);
	}

	public static class Everything {
		@LuaCallable(returnTypes = LuaReturnType.STRING)
		public String test(B target, @Env("env1") D access, @Arg(name = "a") int a, @Optionals @Arg(name = "b") String b, @Arg(name = "var") Integer... v) {
			return access.test();
		}
	}

	@Test
	public void testEverything() {
		MethodDeclaration decl = createMethodDecl(Everything.class);
		decl.validatePositionalArgs(B.class);
		decl.validateOptionalArgs(singleArg("env1", D.class));
	}

	public static class MultiDirect {
		@LuaCallable(returnTypes = { LuaReturnType.NUMBER, LuaReturnType.NUMBER })
		public IMultiReturn test(B target, @Arg(name = "a") int a) {
			return MultiReturn.wrap(a, a + 1);
		}
	}

	@Test
	public void testMultiDirect() {
		createMethodDecl(MultiDirect.class);
	}

	public static class MultiArray {
		@MultipleReturn
		@LuaCallable(returnTypes = { LuaReturnType.NUMBER, LuaReturnType.NUMBER })
		public int[] test(B target, @Arg(name = "a") int a) {
			return new int[] { a, a + 1 };
		}
	}

	@Test
	public void testMultiArray() {
		createMethodDecl(MultiArray.class);
	}

	public static class NonMultiArray {
		@LuaCallable(returnTypes = LuaReturnType.TABLE)
		public int[] test(B target, @Arg(name = "a") int a) {
			return new int[] { a, a + 1 };
		}
	}

	@Test
	public void testNonMultiArray() {
		createMethodDecl(NonMultiArray.class);
	}

	public static class MultiCollection {
		@MultipleReturn
		@LuaCallable(returnTypes = { LuaReturnType.NUMBER, LuaReturnType.NUMBER })
		public List<Integer> test(B target, @Arg(name = "a") int a) {
			return Lists.newArrayList(a, a + 1);
		}
	}

	@Test
	public void testMultiCollection() {
		createMethodDecl(MultiCollection.class);
	}

	public static class NonMultiCollection {
		@LuaCallable(returnTypes = LuaReturnType.TABLE)
		public Collection<Integer> test(B target, @Arg(name = "a") int a) {
			return Sets.newHashSet(a, a + a);
		}
	}

	@Test
	public void testNonMultiCollection() {
		createMethodDecl(NonMultiCollection.class);
	}

	public static class MultiCollectionVoid {
		@MultipleReturn
		@LuaCallable(returnTypes = {})
		public Collection<Integer> test(B target, @Arg(name = "a") int a) {
			return Sets.newHashSet(a, a + a);
		}
	}

	@Test(expected = Exception.class)
	public void testMultiCollectionVoid() {
		createMethodDecl(MultiCollectionVoid.class);
	}

	public static class MultiReturnVoid {
		@LuaCallable(returnTypes = {})
		public IMultiReturn test(B target, @Arg(name = "a") int a) {
			return null;
		}
	}

	@Test(expected = Exception.class)
	public void testMultiReturnVoid() {
		createMethodDecl(MultiReturnVoid.class);
	}

	public static class TwoUnnamed {
		@LuaCallable(returnTypes = LuaReturnType.BOOLEAN)
		public boolean test(B target, Object target2) {
			return true;
		}
	}

	@Test
	public void testTwoUnnamed() {
		createMethodDecl(TwoUnnamed.class);
	}

	public static class UnnamedAfterLua {
		@LuaCallable(returnTypes = LuaReturnType.BOOLEAN)
		public boolean test(@Arg(name = "foo") String arg, B target) {
			return true;
		}
	}

	@Test(expected = MethodDeclaration.ArgumentDefinitionException.class)
	public void testUnnamedAfterLua() {
		createMethodDecl(UnnamedAfterLua.class);
	}

	public static class EnvAfterLua {
		@LuaCallable(returnTypes = LuaReturnType.BOOLEAN)
		public boolean test(@Arg(name = "foo") String arg, @Env("env1") D target) {
			return true;
		}
	}

	@Test(expected = MethodDeclaration.ArgumentDefinitionException.class)
	public void testEnvAfterLua() {
		createMethodDecl(EnvAfterLua.class);
	}

	public static class UnamedAfterEnv {
		@LuaCallable(returnTypes = LuaReturnType.BOOLEAN)
		public boolean test(@Env("env1") D e1, B target) {
			return true;
		}
	}

	@Test(expected = MethodDeclaration.ArgumentDefinitionException.class)
	public void testUnamedAfterEnv() {
		createMethodDecl(UnamedAfterEnv.class);
	}

	public static class OptionalUnnnamed {
		@LuaCallable(returnTypes = LuaReturnType.BOOLEAN)
		public boolean test(@Optionals B target) {
			return true;
		}
	}

	@Test(expected = MethodDeclaration.ArgumentDefinitionException.class)
	public void testOptionalUnnnamed() {
		createMethodDecl(OptionalUnnnamed.class);
	}

	public static class OptionalEnv {
		@LuaCallable(returnTypes = LuaReturnType.BOOLEAN)
		public boolean test(@Optionals @Env("target") B target) {
			return true;
		}
	}

	@Test(expected = MethodDeclaration.ArgumentDefinitionException.class)
	public void testOptionalEnv() {
		createMethodDecl(OptionalEnv.class);
	}

	public static class SameNamedEnv {
		@LuaCallable(returnTypes = LuaReturnType.BOOLEAN)
		public boolean test(@Env("target") B target, @Env("target") B target2) {
			return true;
		}
	}

	@Test(expected = MethodDeclaration.ArgumentDefinitionException.class)
	public void testSameNamedEnv() {
		createMethodDecl(SameNamedEnv.class);
	}

}
