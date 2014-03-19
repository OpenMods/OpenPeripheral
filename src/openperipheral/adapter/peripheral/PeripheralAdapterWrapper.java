package openperipheral.adapter.peripheral;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import openmods.Log;
import openperipheral.adapter.*;
import openperipheral.adapter.method.MethodDeclaration;
import openperipheral.adapter.method.MethodDeclaration.CallWrap;
import openperipheral.adapter.object.IObjectMethodExecutor;
import openperipheral.api.*;

import com.google.common.base.Preconditions;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;

public abstract class PeripheralAdapterWrapper extends AdapterWrapper<IPeripheralMethodExecutor> {

	protected PeripheralAdapterWrapper(Class<?> adapterClass, Class<?> targetClass) {
		super(adapterClass, targetClass);
	}

	static final String ARG_TARGET = "target";
	static final String ARG_COMPUTER = "computer";
	static final String ARG_CONTEXT = "context";

	protected static boolean isIgnoringWarnings(AnnotatedElement element, boolean defaultValue) {
		if (element == null) return defaultValue;
		OnTickSafe ignore = element.getAnnotation(OnTickSafe.class);
		return ignore != null? ignore.value() : defaultValue;
	}

	protected static boolean isOnTick(AnnotatedElement element, boolean defaultValue) {
		OnTick onTick = element.getAnnotation(OnTick.class);
		return onTick != null? onTick.value() : defaultValue;
	}

	protected static abstract class PeripheralMethodExecutor implements IPeripheralMethodExecutor {
		public final MethodDeclaration method;
		public final ExecutionStrategy strategy;
		public final Map<String, Method> proxyArgs;

		protected abstract CallWrap createWrapper(IComputerAccess computer, ILuaContext context, Object target, Object[] luaArgs);

		public PeripheralMethodExecutor(MethodDeclaration method, ExecutionStrategy strategy, Map<String, Method> proxyArgs) {
			this.method = method;
			this.strategy = strategy;
			this.proxyArgs = proxyArgs;
		}

		@Override
		public IDescriptable getWrappedMethod() {
			return method;
		}

		@Override
		public boolean isSynthetic() {
			return false;
		}

		@Override
		public Object[] execute(IComputerAccess computer, ILuaContext context, Object target, Object[] args) throws Exception {
			Callable<Object[]> callable = nameAdapterMethods(target, proxyArgs, createWrapper(computer, context, target, args));
			return strategy.execute(target, computer, context, callable);
		}
	}

	@Override
	protected List<IPeripheralMethodExecutor> buildMethodList() {
		final boolean defaultOnTick = isOnTick(adapterClass, false);

		final boolean packageIsIgnoringWarnings = isIgnoringWarnings(adapterClass.getPackage(), false);
		final boolean classIsIgnoringWarnings = isIgnoringWarnings(adapterClass, packageIsIgnoringWarnings);

		List<IPeripheralMethodExecutor> peripheralMethods = buildMethodList(false, new MethodExecutorFactory<IPeripheralMethodExecutor>() {
			@Override
			public IPeripheralMethodExecutor createExecutor(Method method, MethodDeclaration decl, Map<String, Method> proxyArgs) {
				LuaMethod methodAnn = method.getAnnotation(LuaMethod.class);
				boolean onTick = (methodAnn != null)? methodAnn.onTick() : isOnTick(method, defaultOnTick);

				ExecutionStrategy strategy = onTick? ExecutionStrategy.createOnTickStrategy(targetCls) : ExecutionStrategy.ASYNCHRONOUS;

				if (!strategy.isAlwaysSafe() && !isIgnoringWarnings(method, classIsIgnoringWarnings)) {
					Log.warn("Method '%s' is synchronous, but type %s does not provide world instance. Possible runtime crash!", method, targetCls);
				}

				return PeripheralAdapterWrapper.this.createDirectExecutor(decl, strategy, proxyArgs);
			}
		});

		for (Method m : adapterClass.getMethods()) {
			Include marker = m.getAnnotation(Include.class);
			if (marker != null) includeClass(peripheralMethods, m);
		}

		return peripheralMethods;
	}

	private void includeClass(List<IPeripheralMethodExecutor> result, Method targetProvider) {
		Class<?> target = targetProvider.getReturnType();
		Preconditions.checkArgument(!target.isPrimitive(), "Method %s is marked with annotation 'Include', but returns primitive type", targetProvider);
		AdaptedClass<IObjectMethodExecutor> toInclude = AdapterManager.objects.getAdapterClass(target);
		for (IObjectMethodExecutor objectExecutor : toInclude.getMethods()) {
			if (!objectExecutor.isSynthetic()) result.add(adaptObjectExecutor(targetProvider, objectExecutor));
		}
	}

	protected abstract IPeripheralMethodExecutor createDirectExecutor(MethodDeclaration method, ExecutionStrategy strategy, Map<String, Method> proxyArgs);

	protected abstract IPeripheralMethodExecutor adaptObjectExecutor(Method targetProvider, IObjectMethodExecutor executor);
}
