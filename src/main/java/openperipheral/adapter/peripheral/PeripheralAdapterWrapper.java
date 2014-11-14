package openperipheral.adapter.peripheral;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import openmods.Log;
import openperipheral.adapter.AdapterManager;
import openperipheral.adapter.AdapterWrapper;
import openperipheral.adapter.IDescriptable;
import openperipheral.adapter.composed.ClassMethodsList;
import openperipheral.adapter.method.MethodDeclaration;
import openperipheral.adapter.method.MethodDeclaration.CallWrap;
import openperipheral.adapter.object.IObjectMethodExecutor;
import openperipheral.api.Asynchronous;
import openperipheral.api.Include;
import openperipheral.api.OnTickSafe;

import com.google.common.base.Preconditions;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;

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

	protected static boolean isAsynchronous(AnnotatedElement element, boolean defaultValue) {
		Asynchronous async = element.getAnnotation(Asynchronous.class);
		return async != null? async.value() : defaultValue;
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
			final Callable<Object[]> callable = nameAdapterMethods(target, proxyArgs, createWrapper(computer, context, target, args));
			return strategy.execute(target, computer, context, callable);
		}
	}

	@Override
	protected List<IPeripheralMethodExecutor> buildMethodList() {
		final boolean defaultAsync = isAsynchronous(adapterClass, false);

		final boolean packageIsIgnoringWarnings = isIgnoringWarnings(adapterClass.getPackage(), false);
		final boolean classIsIgnoringWarnings = isIgnoringWarnings(adapterClass, packageIsIgnoringWarnings);

		List<IPeripheralMethodExecutor> peripheralMethods = buildMethodList(false, new MethodExecutorFactory<IPeripheralMethodExecutor>() {
			@Override
			public IPeripheralMethodExecutor createExecutor(Method method, MethodDeclaration decl, Map<String, Method> proxyArgs) {
				boolean isAsync = isAsynchronous(method, defaultAsync);

				ExecutionStrategy strategy = isAsync? ExecutionStrategy.ASYNCHRONOUS : ExecutionStrategy.createOnTickStrategy(targetCls);

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
		ClassMethodsList<IObjectMethodExecutor> toInclude = AdapterManager.objects.getAdapterClass(target);
		for (IObjectMethodExecutor objectExecutor : toInclude.listMethods()) {
			if (!objectExecutor.isSynthetic()) result.add(adaptObjectExecutor(targetProvider, objectExecutor));
		}
	}

	protected abstract IPeripheralMethodExecutor createDirectExecutor(MethodDeclaration method, ExecutionStrategy strategy, Map<String, Method> proxyArgs);

	protected abstract IPeripheralMethodExecutor adaptObjectExecutor(Method targetProvider, IObjectMethodExecutor executor);
}
