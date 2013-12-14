package openperipheral.adapter.peripheral;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;

import openmods.Log;
import openperipheral.adapter.AdaptedClass;
import openperipheral.adapter.AdapterManager;
import openperipheral.adapter.AdapterWrapper;
import openperipheral.adapter.MethodDeclaration;
import openperipheral.adapter.object.IObjectMethodExecutor;
import openperipheral.api.Include;
import openperipheral.api.LuaMethod;
import openperipheral.api.OnTick;
import openperipheral.api.OnTickSafe;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

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

		protected abstract Callable<Object[]> createWrapper(IComputerAccess computer, ILuaContext context, Object target, Object[] luaArgs);

		public PeripheralMethodExecutor(MethodDeclaration method, ExecutionStrategy strategy) {
			this.method = method;
			this.strategy = strategy;
		}

		@Override
		public MethodDeclaration getWrappedMethod() {
			return method;
		}

		@Override
		public boolean isSynthetic() {
			return false;
		}

		@Override
		public Object[] execute(IComputerAccess computer, ILuaContext context, Object target, Object[] args) throws Exception {
			Callable<Object[]> callable = createWrapper(computer, context, target, args);
			return strategy.execute(target, computer, context, callable);
		}
	}

	@Override
	protected Map<String, IPeripheralMethodExecutor> buildMethodMap() {

		final boolean defaultOnTick = isOnTick(adapterClass, false);
		final boolean defaultIsIgnoringWarnings = isIgnoringWarnings(adapterClass, false);

		Map<String, IPeripheralMethodExecutor> peripheralMethods = buildMethodMap(false, new MethodExecutorFactory<IPeripheralMethodExecutor>() {
			@Override
			public IPeripheralMethodExecutor createExecutor(Method method, MethodDeclaration decl) {
				LuaMethod methodAnn = method.getAnnotation(LuaMethod.class);
				boolean onTick = (methodAnn != null)? methodAnn.onTick() : isOnTick(method, defaultOnTick);

				ExecutionStrategy strategy = onTick? ExecutionStrategy.createOnTickStrategy(targetCls) : ExecutionStrategy.ASYNCHRONOUS;

				if (!(isIgnoringWarnings(method, defaultIsIgnoringWarnings) || strategy.isAlwaysSafe())) {
					Log.warn("Method '%s' is synchronous, but type %s does not provide world instance. Possible runtime crash!", method, targetCls);
				}

				return PeripheralAdapterWrapper.this.createDirectExecutor(decl, strategy);
			}
		});

		ImmutableMap.Builder<String, IPeripheralMethodExecutor> builder = ImmutableMap.builder();
		builder.putAll(peripheralMethods);

		for (Method m : adapterClass.getMethods()) {
			Include marker = m.getAnnotation(Include.class);
			if (marker != null) {
				includeClass(builder, m);
			}
		}

		return builder.build();
	}

	private void includeClass(ImmutableMap.Builder<String, IPeripheralMethodExecutor> result, Method targetProvider) {
		Class<?> target = targetProvider.getReturnType();
		Preconditions.checkArgument(!target.isPrimitive(), "Method %s is marked with annotation 'Include', but returns primitive type", targetProvider);
		AdaptedClass<IObjectMethodExecutor> toInclude = AdapterManager.objects.getAdapterClass(target);
		for (IObjectMethodExecutor objectExecutor : toInclude.getMethods()) {
			if (!objectExecutor.isSynthetic()) {
				IPeripheralMethodExecutor peripheralExecutor = adaptObjectExecutor(targetProvider, objectExecutor);
				result.put(peripheralExecutor.getWrappedMethod().name, peripheralExecutor);
			}
		}
	}

	protected abstract IPeripheralMethodExecutor createDirectExecutor(MethodDeclaration method, ExecutionStrategy strategy);

	protected abstract IPeripheralMethodExecutor adaptObjectExecutor(Method targetProvider, IObjectMethodExecutor executor);
}
