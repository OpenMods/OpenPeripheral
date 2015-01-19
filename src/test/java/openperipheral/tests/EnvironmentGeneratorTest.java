package openperipheral.tests;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Map;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import openperipheral.adapter.*;
import openperipheral.interfaces.oc.asm.EnvironmentFactory;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public class EnvironmentGeneratorTest {

	public static interface InterfaceA {
		public void testA(int a);

		public String testB(float a);
	}

	public static interface InterfaceB {
		public String testB(float a);

		public float testC(int a);
	}

	private abstract static class TargetClass implements InterfaceA, InterfaceB {

	}

	private static Method getMethod(Class<?> cls, String prefix) {
		for (Method m : cls.getMethods())
			if (m.getName().startsWith(prefix)) return m;

		throw new IllegalArgumentException();
	}

	private static void addMethod(Map<String, Pair<IMethodExecutor, IMethodCall>> methods, String name, final boolean isAsynchronous, final String desc) {
		IMethodExecutor executor = mock(IMethodExecutor.class);

		when(executor.isAsynchronous()).thenReturn(isAsynchronous);

		IDescriptable descriptable = mock(IDescriptable.class);
		when(descriptable.signature()).thenReturn(desc);
		when(executor.description()).thenReturn(descriptable);

		IMethodCall call = mock(IMethodCall.class);
		when(executor.startCall(anyObject())).thenReturn(call);

		methods.put(name, Pair.of(executor, call));
	}

	private static void testMethod(Object target, Object o, Class<?> cls, String name, IMethodExecutor executor, IMethodCall call) throws Exception {
		Method m = getMethod(cls, name.substring(0, 1));

		Callback callback = m.getAnnotation(Callback.class);
		Assert.assertNotNull(callback);

		Assert.assertEquals(executor.isAsynchronous(), callback.direct());
		Assert.assertEquals(executor.description().signature(), callback.doc());

		Arguments args = mock(Arguments.class);
		final Object[] result = new Object[] { 1, 2, 3 };
		when(args.toArray()).thenReturn(result);
		when(call.setOptionalArg(anyString(), anyObject())).thenReturn(call);
		Context context = mock(Context.class);

		m.invoke(o, context, args);

		verify(executor).startCall(target);

		verify(args).toArray();
		verify(call).setOptionalArg(DefaultArgNames.ARG_CONTEXT, context);
		verify(call).call(result);
	}

	@Test
	public void test() throws Exception {
		Map<String, Pair<IMethodExecutor, IMethodCall>> mocks = Maps.newHashMap();

		// 7 methods, to generate ICONST_0...ICONST_5 and then LDC 6

		addMethod(mocks, "a1", true, "desc1");
		addMethod(mocks, "b2_", false, "desc2");
		addMethod(mocks, "c3", true, "desc3");
		addMethod(mocks, "d 4", false, "desc4");
		addMethod(mocks, "e*5", true, "desc5");
		addMethod(mocks, "f6", false, "desc6");
		addMethod(mocks, "gG", true, "desc-");

		Map<String, IMethodExecutor> methods = Maps.newHashMap();
		for (Map.Entry<String, Pair<IMethodExecutor, IMethodCall>> e : mocks.entrySet())
			methods.put(e.getKey(), e.getValue().getLeft());

		EnvironmentFactory generator = new EnvironmentFactory();

		Class<?> cls = generator.generateEnvironment("TestClass", TargetClass.class, ImmutableSet.of(InterfaceA.class, InterfaceB.class), new WrappedEntityBase(methods));

		final TargetClass target = mock(TargetClass.class);
		Object o = cls.getConstructor(TargetClass.class).newInstance(target);

		Assert.assertTrue(o instanceof ManagedEnvironment);
		Assert.assertTrue(o instanceof InterfaceA);
		Assert.assertTrue(o instanceof InterfaceB);

		when(target.testB(anyInt())).thenReturn("abcd");

		InterfaceA aa = (InterfaceA)o;
		Assert.assertEquals(aa.testB(3), "abcd");
		verify(target).testB(3);

		for (Map.Entry<String, Pair<IMethodExecutor, IMethodCall>> method : mocks.entrySet()) {
			final Pair<IMethodExecutor, IMethodCall> value = method.getValue();
			testMethod(target, o, cls, method.getKey(), value.getLeft(), value.getRight());
		}
	}
}
