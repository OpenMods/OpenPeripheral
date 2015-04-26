package openperipheral.tests;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import li.cil.oc.api.detail.Builder.ComponentBuilder;
import li.cil.oc.api.detail.Builder.NodeBuilder;
import li.cil.oc.api.detail.NetworkAPI;
import li.cil.oc.api.machine.*;
import li.cil.oc.api.network.*;
import openperipheral.adapter.*;
import openperipheral.adapter.IMethodDescription.IArgumentDescription;
import openperipheral.adapter.composed.IndexedMethodMap;
import openperipheral.api.Constants;
import openperipheral.api.adapter.method.ReturnType;
import openperipheral.api.architecture.IArchitectureAccess;
import openperipheral.api.architecture.IAttachable;
import openperipheral.api.architecture.oc.IOpenComputersAttachable;
import openperipheral.api.converter.IConverter;
import openperipheral.converter.TypeConvertersProvider;
import openperipheral.interfaces.oc.asm.ICodeGenerator;
import openperipheral.interfaces.oc.asm.MethodsStore;
import openperipheral.interfaces.oc.asm.object.ObjectCodeGenerator;
import openperipheral.interfaces.oc.asm.peripheral.PeripheralCodeGenerator;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public class EnvironmentGeneratorTest {

	private static Method defineClass;

	static {
		try {
			defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
			defineClass.setAccessible(true);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> Class<T> defineClass(String className, byte[] bytes) {
		try {
			return (Class<T>)defineClass.invoke(getClass().getClassLoader(), className, bytes, 0, bytes.length);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	private <T> Class<T> generateClass(String name, Class<?> targetClass, Set<Class<?>> interfaces, Map<String, IMethodExecutor> methods, ICodeGenerator generator) {
		final IndexedMethodMap methodMap = new IndexedMethodMap(methods);
		int methodId = MethodsStore.drop(methodMap.getMethods());
		byte[] bytes = generator.generate(name, targetClass, interfaces, methodMap, methodId);
		Class<T> cls = defineClass(name, bytes);
		return cls;
	}

	public static interface InterfaceA {
		public void testA(int a);

		public String testB(float a);
	}

	public static interface InterfaceB {
		public String testB(float a);

		public float testC(int a);
	}

	private abstract static class TargetClass implements InterfaceA, InterfaceB {}

	private abstract static class AwareTargetClass implements IOpenComputersAttachable, IAttachable {}

	private abstract static class SemiAwareTargetClass implements IOpenComputersAttachable {}

	private abstract static class CommonAwareTargetClass implements IAttachable {}

	private static Method getMethod(Class<?> cls, String prefix) {
		for (Method m : cls.getMethods())
			if (m.getName().startsWith(prefix)) return m;

		throw new IllegalArgumentException();
	}

	private static void addMethod(Map<String, Pair<IMethodExecutor, IMethodCall>> methods, String name, final boolean isAsynchronous, final String desc) {
		IMethodExecutor executor = mock(IMethodExecutor.class);

		when(executor.isAsynchronous()).thenReturn(isAsynchronous);

		IMethodDescription descriptable = mock(IMethodDescription.class);

		when(descriptable.arguments()).thenReturn(ImmutableList.<IArgumentDescription> of());
		when(descriptable.returnTypes()).thenReturn(ImmutableList.<ReturnType> of());
		when(descriptable.description()).thenReturn("");

		when(executor.description()).thenReturn(descriptable);

		IMethodCall call = mock(IMethodCall.class);
		when(executor.startCall(anyObject())).thenReturn(call);

		methods.put(name, Pair.of(executor, call));
	}

	private static void testMethods(Map<String, Pair<IMethodExecutor, IMethodCall>> executorMocks, Class<?> generatedClass, final TargetClass target, Object wrapper) throws Exception {
		for (Map.Entry<String, Pair<IMethodExecutor, IMethodCall>> method : executorMocks.entrySet()) {
			final Pair<IMethodExecutor, IMethodCall> value = method.getValue();
			testMethod(target, wrapper, generatedClass, method.getKey(), value.getLeft(), value.getRight());
		}
	}

	private static void testMethod(Object target, Object wrapper, Class<?> generatedClass, String name, IMethodExecutor executor, IMethodCall call) throws Exception {
		Method m = getMethod(generatedClass, name.substring(0, 1));

		Callback callback = m.getAnnotation(Callback.class);
		Assert.assertNotNull(callback);

		Assert.assertEquals(executor.isAsynchronous(), callback.direct());

		// that's what I get for not injecting ...
		Assert.assertEquals("function()", callback.doc());

		Arguments args = mock(Arguments.class);
		final Object[] argArray = new Object[] { 1, 2, 3 };
		when(args.toArray()).thenReturn(argArray);
		when(call.setOptionalArg(eq(Constants.ARG_CONVERTER), anyObject())).thenReturn(call);
		when(call.setOptionalArg(anyString(), anyObject())).thenReturn(call);
		Context context = mock(Context.class);

		m.invoke(wrapper, context, args);

		verify(executor).startCall(target);

		verify(args).toArray();
		verify(call).setOptionalArg(Constants.ARG_CONTEXT, context);
		verify(call).call(argArray);
	}

	private static void verifyCallThrough(final TargetClass wrapped, Object wrapper) {
		when(wrapped.testB(anyInt())).thenReturn("abcd");

		InterfaceA aa = (InterfaceA)wrapper;
		Assert.assertEquals(aa.testB(3), "abcd");
		verify(wrapped).testB(3);
	}

	private static void addDefaultMethods(Map<String, Pair<IMethodExecutor, IMethodCall>> mocks) {
		// 7 methods, to generate ICONST_0...ICONST_5 and then LDC 6

		addMethod(mocks, "a1", true, "desc1");
		addMethod(mocks, "b2_", false, "desc2");
		addMethod(mocks, "c3", true, "desc3");
		addMethod(mocks, "d 4", false, "desc4");
		addMethod(mocks, "e*5", true, "desc5");
		addMethod(mocks, "f6", false, "desc6");
		addMethod(mocks, "gG", true, "desc-");
	}

	private static Map<String, IMethodExecutor> extractExecutors(Map<String, Pair<IMethodExecutor, IMethodCall>> mocks) {
		Map<String, IMethodExecutor> methods = Maps.newHashMap();
		for (Map.Entry<String, Pair<IMethodExecutor, IMethodCall>> e : mocks.entrySet())
			methods.put(e.getKey(), e.getValue().getLeft());
		return methods;
	}

	private static IConverter setupConverterMock() {
		IConverter converter = mock(IConverter.class);
		TypeConvertersProvider.INSTANCE.registerConverter(Constants.ARCH_OPEN_COMPUTERS, converter);
		return converter;
	}

	@Test
	public void testPeripheral() throws Exception {
		setupConverterMock();

		setupOpenComputersApiMock();

		Map<String, Pair<IMethodExecutor, IMethodCall>> mocks = Maps.newHashMap();
		addDefaultMethods(mocks);
		Map<String, IMethodExecutor> methods = extractExecutors(mocks);

		ICodeGenerator generator = new PeripheralCodeGenerator();

		Class<?> cls = generateClass("TestClass\u2652", TargetClass.class, ImmutableSet.of(InterfaceA.class, InterfaceB.class), methods, generator);

		final TargetClass target = mock(TargetClass.class);
		Object o = cls.getConstructor(TargetClass.class).newInstance(target);

		Assert.assertTrue(o instanceof ManagedEnvironment);
		Assert.assertTrue(o instanceof InterfaceA);
		Assert.assertTrue(o instanceof InterfaceB);

		verifyCallThrough(target, o);

		testMethods(mocks, cls, target, o);
	}

	@Test(expected = IllegalStateException.class)
	public void testPeripheralNullTarget() throws Throwable {
		setupConverterMock();
		setupOpenComputersApiMock();

		Map<String, Pair<IMethodExecutor, IMethodCall>> mocks = Maps.newHashMap();
		addMethod(mocks, "a1", true, "desc1");

		Map<String, IMethodExecutor> methods = extractExecutors(mocks);

		ICodeGenerator generator = new PeripheralCodeGenerator();

		Class<?> cls = generateClass("TestClass\u2655", TargetClass.class, ImmutableSet.<Class<?>> of(), methods, generator);

		Object o = cls.getConstructor(TargetClass.class).newInstance(new Object[] { null });

		Assert.assertTrue(o instanceof ManagedEnvironment);

		Method m = getMethod(cls, "a1");

		Arguments args = mock(Arguments.class);
		Context context = mock(Context.class);

		try {
			m.invoke(o, context, args);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	public void testObject() throws Exception {
		setupConverterMock();

		Map<String, Pair<IMethodExecutor, IMethodCall>> mocks = Maps.newHashMap();
		addDefaultMethods(mocks);

		Map<String, IMethodExecutor> methods = extractExecutors(mocks);

		ICodeGenerator generator = new ObjectCodeGenerator();

		Class<?> cls = generateClass("TestClass\u2656", TargetClass.class, ImmutableSet.of(InterfaceA.class, InterfaceB.class), methods, generator);

		final TargetClass target = mock(TargetClass.class);
		Object o = cls.getConstructor(TargetClass.class).newInstance(target);

		Assert.assertTrue(o instanceof Value);
		Assert.assertTrue(o instanceof InterfaceA);
		Assert.assertTrue(o instanceof InterfaceB);

		verifyCallThrough(target, o);

		testMethods(mocks, cls, target, o);
	}

	@Test(expected = IllegalStateException.class)
	public void testObjectNullConstructor() throws Throwable {
		setupConverterMock();

		Map<String, Pair<IMethodExecutor, IMethodCall>> mocks = Maps.newHashMap();

		addMethod(mocks, "a1", true, "desc1");

		Map<String, IMethodExecutor> methods = extractExecutors(mocks);

		ICodeGenerator generator = new ObjectCodeGenerator();

		Class<?> cls = generateClass("TestClass\u2657", TargetClass.class, ImmutableSet.<Class<?>> of(), methods, generator);

		// argument-less ctor, for serialization
		Object o = cls.getConstructor().newInstance();

		Assert.assertTrue(o instanceof Value);

		Method m = getMethod(cls, "a1");

		Arguments args = mock(Arguments.class);
		Context context = mock(Context.class);

		try {
			m.invoke(o, context, args);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private interface ContextEnvironment extends Context, Environment {}

	private static IArchitectureAccess verifyCommonConnectCall(IAttachable target, String nodeAddress) {
		ArgumentCaptor<IArchitectureAccess> connectAccess = ArgumentCaptor.forClass(IArchitectureAccess.class);
		verify(target).addComputer(connectAccess.capture());
		Assert.assertEquals(nodeAddress, connectAccess.getValue().callerName());
		return connectAccess.getValue();
	}

	private static void verifyCommonDisconnectCall(IAttachable target, IArchitectureAccess connectAccess) {
		ArgumentCaptor<IArchitectureAccess> disconnectAccess = ArgumentCaptor.forClass(IArchitectureAccess.class);
		verify(target).removeComputer(disconnectAccess.capture());
		Assert.assertEquals(connectAccess, disconnectAccess.getValue()); // must be same object
	}

	private static void verifyOcSpecificConnectCall(IOpenComputersAttachable target, Node node) {
		ArgumentCaptor<Node> capuredNode = ArgumentCaptor.forClass(Node.class);
		verify(target).onConnect(capuredNode.capture());
		Assert.assertEquals(node, capuredNode.getValue());
	}

	private static void verifyOcSpecificDisconnectCall(IOpenComputersAttachable target, Node node) {
		ArgumentCaptor<Node> capuredNode = ArgumentCaptor.forClass(Node.class);
		verify(target).onConnect(capuredNode.capture());
		Assert.assertEquals(node, capuredNode.getValue());
	}

	@Test
	public void testConnectivity() throws Exception {
		setupOpenComputersApiMock();

		ICodeGenerator generator = new PeripheralCodeGenerator();

		Map<String, IMethodExecutor> methods = Maps.newHashMap();
		Class<? extends ManagedEnvironment> cls = generateClass("TestClass\u2653", AwareTargetClass.class, ImmutableSet.<Class<?>> of(), methods, generator);

		final AwareTargetClass target = mock(AwareTargetClass.class);
		ManagedEnvironment o = cls.getConstructor(AwareTargetClass.class).newInstance(target);

		Environment environment = mock(ContextEnvironment.class);
		Node node = mock(Node.class);
		final String nodeAddress = "node_11";
		when(node.address()).thenReturn(nodeAddress);
		when(node.host()).thenReturn(environment);
		when(environment.node()).thenReturn(node);

		o.onConnect(node);
		final IArchitectureAccess connectAccess = verifyCommonConnectCall(target, nodeAddress);
		verifyOcSpecificConnectCall(target, node);

		o.onDisconnect(node);
		verifyCommonDisconnectCall(target, connectAccess);
		verifyOcSpecificDisconnectCall(target, node);
	}

	@Test
	public void testNodeConnectivity() throws Exception {
		setupOpenComputersApiMock();

		ICodeGenerator generator = new PeripheralCodeGenerator();

		Map<String, IMethodExecutor> methods = Maps.newHashMap();
		Class<? extends ManagedEnvironment> cls = generateClass("TestClass\u2654", SemiAwareTargetClass.class, ImmutableSet.<Class<?>> of(), methods, generator);

		final SemiAwareTargetClass target = mock(SemiAwareTargetClass.class);
		ManagedEnvironment o = cls.getConstructor(SemiAwareTargetClass.class).newInstance(target);

		Node node = mock(Node.class);

		o.onConnect(node);
		verifyOcSpecificConnectCall(target, node);

		o.onDisconnect(node);
		verifyOcSpecificDisconnectCall(target, node);
	}

	@Test
	public void testCommonConnectivity() throws Exception {
		setupOpenComputersApiMock();

		ICodeGenerator generator = new PeripheralCodeGenerator();

		Map<String, IMethodExecutor> methods = Maps.newHashMap();
		Class<? extends ManagedEnvironment> cls = generateClass("TestClass\u2658", CommonAwareTargetClass.class, ImmutableSet.<Class<?>> of(), methods, generator);

		final CommonAwareTargetClass target = mock(CommonAwareTargetClass.class);
		ManagedEnvironment o = cls.getConstructor(CommonAwareTargetClass.class).newInstance(target);

		Environment environment = mock(ContextEnvironment.class);
		Node node = mock(Node.class);
		final String nodeAddress = "node_13";
		when(node.address()).thenReturn(nodeAddress);
		when(node.host()).thenReturn(environment);
		when(environment.node()).thenReturn(node);

		o.onConnect(node);
		IArchitectureAccess connectAccess = verifyCommonConnectCall(target, nodeAddress);

		o.onDisconnect(node);
		verifyCommonDisconnectCall(target, connectAccess);
	}

	private static void setupOpenComputersApiMock() {
		final NodeBuilder nodeBuilderMock = mock(NodeBuilder.class);
		final ComponentBuilder componentBuilderMock = mock(ComponentBuilder.class);
		final NetworkAPI networkMock = mock(NetworkAPI.class);
		li.cil.oc.api.API.network = networkMock;
		when(networkMock.newNode(any(Environment.class), any(Visibility.class))).thenReturn(nodeBuilderMock);
		when(nodeBuilderMock.withComponent(anyString())).thenReturn(componentBuilderMock);
		when(componentBuilderMock.create()).thenReturn(null);
	}
}
