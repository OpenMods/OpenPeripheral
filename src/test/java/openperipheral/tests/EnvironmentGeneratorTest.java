package openperipheral.tests;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Map;

import li.cil.oc.api.detail.Builder.ComponentBuilder;
import li.cil.oc.api.detail.Builder.NodeBuilder;
import li.cil.oc.api.detail.NetworkAPI;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.*;
import openperipheral.adapter.*;
import openperipheral.adapter.composed.IndexedMethodMap;
import openperipheral.api.Constants;
import openperipheral.api.architecture.IArchitectureAccess;
import openperipheral.api.architecture.IAttachable;
import openperipheral.api.architecture.oc.IOpenComputersAttachable;
import openperipheral.api.converter.IConverter;
import openperipheral.converter.TypeConvertersProvider;
import openperipheral.interfaces.oc.asm.EnvironmentFactory;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

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

	private abstract static class AwareTargetClass implements IOpenComputersAttachable, IAttachable {

	}

	private abstract static class SemiAwareTargetClass implements IOpenComputersAttachable {

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
		when(descriptable.doc()).thenReturn(desc);
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
		Assert.assertEquals(executor.description().doc(), callback.doc());

		Arguments args = mock(Arguments.class);
		final Object[] result = new Object[] { 1, 2, 3 };
		when(args.toArray()).thenReturn(result);
		when(call.setOptionalArg(eq(Constants.ARG_CONVERTER), anyObject())).thenReturn(call);
		when(call.setOptionalArg(anyString(), anyObject())).thenReturn(call);
		Context context = mock(Context.class);

		m.invoke(o, context, args);

		verify(executor).startCall(target);

		verify(args).toArray();
		verify(call).setOptionalArg(Constants.ARG_CONTEXT, context);
		verify(call).call(result);
	}

	@Test
	public void test() throws Exception {
		IConverter converter = mock(IConverter.class); // Dependency injection? Office hours only!

		configureApi();

		TypeConvertersProvider.INSTANCE.registerConverter(Constants.ARCH_OPEN_COMPUTERS, converter);

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

		Class<?> cls = generator.generateEnvironment("TestClass\u2652", TargetClass.class, ImmutableSet.of(InterfaceA.class, InterfaceB.class), new IndexedMethodMap(methods));

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

	private interface ContextNode extends Context, Node {}

	@Test
	public void testConnectivity() throws Exception {
		configureApi();

		EnvironmentFactory generator = new EnvironmentFactory();

		Map<String, IMethodExecutor> methods = Maps.newHashMap();
		Class<? extends ManagedEnvironment> cls = generator.generateEnvironment("TestClass\u2653", AwareTargetClass.class, ImmutableSet.<Class<?>> of(), new IndexedMethodMap(methods));

		final AwareTargetClass target = mock(AwareTargetClass.class);
		ManagedEnvironment o = cls.getConstructor(AwareTargetClass.class).newInstance(target);

		ContextNode contextNode = mock(ContextNode.class);
		final String nodeAddress = "node_11";
		when(contextNode.address()).thenReturn(nodeAddress);
		when(contextNode.node()).thenReturn(contextNode);

		o.onConnect(contextNode);

		ArgumentCaptor<IArchitectureAccess> connectAccess = ArgumentCaptor.forClass(IArchitectureAccess.class);
		verify(target).addComputer(connectAccess.capture());
		Assert.assertEquals(nodeAddress, connectAccess.getValue().callerName());

		{
			ArgumentCaptor<Node> node = ArgumentCaptor.forClass(Node.class);
			verify(target).onConnect(node.capture());
			Assert.assertEquals(contextNode, node.getValue());
		}

		o.onDisconnect(contextNode);

		ArgumentCaptor<IArchitectureAccess> disconnectAccess = ArgumentCaptor.forClass(IArchitectureAccess.class);
		verify(target).removeComputer(disconnectAccess.capture());
		Assert.assertEquals(connectAccess.getValue(), disconnectAccess.getValue()); // must be same object

		{
			ArgumentCaptor<Node> node = ArgumentCaptor.forClass(Node.class);
			verify(target).onConnect(node.capture());
			Assert.assertEquals(contextNode, node.getValue());
		}
	}

	@Test
	public void testNodeConnectivity() throws Exception {
		configureApi();

		EnvironmentFactory generator = new EnvironmentFactory();

		Map<String, IMethodExecutor> methods = Maps.newHashMap();
		Class<? extends ManagedEnvironment> cls = generator.generateEnvironment("TestClass\u2654", SemiAwareTargetClass.class, ImmutableSet.<Class<?>> of(), new IndexedMethodMap(methods));

		final SemiAwareTargetClass target = mock(SemiAwareTargetClass.class);
		ManagedEnvironment o = cls.getConstructor(SemiAwareTargetClass.class).newInstance(target);

		ContextNode contextNode = mock(ContextNode.class);
		final String nodeAddress = "node_12";
		when(contextNode.address()).thenReturn(nodeAddress);
		when(contextNode.node()).thenReturn(contextNode);

		o.onConnect(contextNode);

		{
			ArgumentCaptor<Node> node = ArgumentCaptor.forClass(Node.class);
			verify(target).onConnect(node.capture());
			Assert.assertEquals(contextNode, node.getValue());
		}

		o.onDisconnect(contextNode);

		{
			ArgumentCaptor<Node> node = ArgumentCaptor.forClass(Node.class);
			verify(target).onConnect(node.capture());
			Assert.assertEquals(contextNode, node.getValue());
		}
	}

	private static void configureApi() {
		final NodeBuilder nodeBuilderMock = mock(NodeBuilder.class);
		final ComponentBuilder componentBuilderMock = mock(ComponentBuilder.class);
		final NetworkAPI networkMock = mock(NetworkAPI.class);
		li.cil.oc.api.API.network = networkMock;
		when(networkMock.newNode(any(Environment.class), any(Visibility.class))).thenReturn(nodeBuilderMock);
		when(nodeBuilderMock.withComponent(anyString())).thenReturn(componentBuilderMock);
		when(componentBuilderMock.create()).thenReturn(null);
	}
}
