package openperipheral.adapter.peripheral;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import openmods.Log;
import openperipheral.adapter.*;
import openperipheral.adapter.PropertyListBuilder.FieldContext;
import openperipheral.adapter.PropertyListBuilder.IPropertyExecutorFactory;
import openperipheral.adapter.PropertyListBuilder.PropertyExecutor;
import openperipheral.adapter.method.MethodDeclaration;
import openperipheral.adapter.method.MethodDeclaration.CallWrap;
import openperipheral.adapter.object.IObjectMethodExecutor;
import openperipheral.api.PeripheralTypeId;

import com.google.common.base.Preconditions;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class PeripheralInlineAdapterWrapper extends PeripheralAdapterWrapper implements IPropertyExecutorFactory<IPeripheralMethodExecutor> {

	private static class InlineMethodExecutor extends PeripheralMethodExecutor {

		public InlineMethodExecutor(MethodDeclaration method, ExecutionStrategy strategy) {
			super(method, strategy);
		}

		@Override
		protected CallWrap createWrapper(IComputerAccess computer, ILuaContext context, Object target, Object[] luaArgs) {
			return method.createWrapper(target)
					.setJavaArg(ARG_COMPUTER, computer)
					.setJavaArg(ARG_CONTEXT, context)
					.setLuaArgs(luaArgs);
		}
	}

	private final String source;

	private static String getSourceId(Class<?> cls) {
		PeripheralTypeId id = cls.getAnnotation(PeripheralTypeId.class);
		if (id != null) return id.value();
		Log.trace("Inline adapter %s has no PeripheralTypeId annotation", cls);
		return cls.getName().toLowerCase();
	}

	public PeripheralInlineAdapterWrapper(Class<?> targetClass) {
		this(targetClass, getSourceId(targetClass));
	}

	private PeripheralInlineAdapterWrapper(Class<?> targetClass, String source) {
		super(targetClass, targetClass, source);
		this.source = source;
	}

	@Override
	protected IPeripheralMethodExecutor createDirectExecutor(MethodDeclaration method, ExecutionStrategy strategy) {
		return new InlineMethodExecutor(method, strategy);
	}

	@Override
	protected void configureJavaArguments(MethodDeclaration decl) {
		decl.declareJavaArgType(ARG_COMPUTER, IComputerAccess.class);
		decl.declareJavaArgType(ARG_CONTEXT, ILuaContext.class);
	}

	@Override
	protected IPeripheralMethodExecutor adaptObjectExecutor(final Method targetProvider, final IObjectMethodExecutor executor) {
		Preconditions.checkArgument(Arrays.equals(targetProvider.getParameterTypes(), new Class<?>[] {}));
		return new IPeripheralMethodExecutor() {

			@Override
			public IDescriptable description() {
				return executor.description();
			}

			@Override
			public Object[] execute(IComputerAccess computer, ILuaContext context, Object target, Object[] args) throws Exception {
				Object executorTarget = targetProvider.invoke(target);
				return executor.execute(context, executorTarget, args);
			}
		};
	}

	private static class PeripheralPropertyExecutor extends PropertyExecutor implements IPeripheralMethodExecutor {

		protected PeripheralPropertyExecutor(FieldContext context) {
			super(context);
		}

		@Override
		public Object[] execute(IComputerAccess computer, ILuaContext context, Object target, Object[] args) {
			return call(target, args);
		}

	}

	@Override
	public IPeripheralMethodExecutor createExecutor(FieldContext context) {
		return new PeripheralPropertyExecutor(context);
	}

	@Override
	protected List<IPeripheralMethodExecutor> buildMethodList() {
		List<IPeripheralMethodExecutor> result = super.buildMethodList();
		PropertyListBuilder.buildPropertyList(targetClass, source, this, result);
		return result;
	}

	@Override
	public String describe() {
		return "internal periperal (source: " + adapterClass.toString() + ")";
	}
}