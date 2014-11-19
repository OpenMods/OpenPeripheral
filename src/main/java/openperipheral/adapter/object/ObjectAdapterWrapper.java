package openperipheral.adapter.object;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;

import openmods.Log;
import openperipheral.adapter.*;
import openperipheral.adapter.PropertyListBuilder.FieldContext;
import openperipheral.adapter.PropertyListBuilder.IPropertyExecutorFactory;
import openperipheral.adapter.PropertyListBuilder.PropertyExecutor;
import openperipheral.adapter.method.MethodDeclaration;
import openperipheral.api.IAdapter;
import openperipheral.api.ObjectTypeId;
import dan200.computercraft.api.lua.ILuaContext;

public abstract class ObjectAdapterWrapper extends AdapterWrapper<IObjectMethodExecutor> {

	protected ObjectAdapterWrapper(Class<?> adapterClass, Class<?> targetClass, String source) {
		super(adapterClass, targetClass, source);
	}

	private static final String ARG_TARGET = "target";
	private static final String ARG_CONTEXT = "context";

	@Override
	protected List<IObjectMethodExecutor> buildMethodList() {
		return buildMethodList(new MethodExecutorFactory<IObjectMethodExecutor>() {
			@Override
			public IObjectMethodExecutor createExecutor(Method method, final MethodDeclaration decl) {
				return new IObjectMethodExecutor() {

					@Override
					public IDescriptable description() {
						return decl;
					}

					@Override
					public Object[] execute(ILuaContext context, Object target, Object[] args) throws Exception {
						return createWrapper(decl, context, target, args).call();
					}
				};
			}
		});
	}

	protected abstract Callable<Object[]> createWrapper(MethodDeclaration decl, ILuaContext context, Object target, Object[] args);

	private static class ObjectPropertyExecutor extends PropertyExecutor implements IObjectMethodExecutor {

		protected ObjectPropertyExecutor(FieldContext context) {
			super(context);
		}

		@Override
		public Object[] execute(ILuaContext context, Object target, Object[] args) {
			return call(target, args);
		}

	}

	public static class External extends ObjectAdapterWrapper {

		private final IAdapter adapter;

		public External(IAdapter adapter) {
			super(adapter.getClass(), adapter.getTargetClass(), adapter.getSourceId());
			this.adapter = adapter;
		}

		@Override
		protected void configureJavaArguments(MethodDeclaration decl) {
			decl.setDefaultArgName(0, ARG_TARGET);

			decl.declareJavaArgType(ARG_CONTEXT, ILuaContext.class);
			decl.declareJavaArgType(ARG_TARGET, targetClass);
		}

		@Override
		protected Callable<Object[]> createWrapper(MethodDeclaration decl, ILuaContext context, Object target, Object[] args) {
			return decl.createWrapper(adapter)
					.setJavaArg(ARG_TARGET, target)
					.setJavaArg(ARG_CONTEXT, context)
					.setLuaArgs(args);
		}

		@Override
		public String describe() {
			return "external object (source: " + adapterClass.toString() + ")";
		}
	}

	public static class Inline extends ObjectAdapterWrapper implements IPropertyExecutorFactory<IObjectMethodExecutor> {

		private final String source;

		public Inline(Class<?> targetClass) {
			this(targetClass, getSourceId(targetClass));
		}

		private Inline(Class<?> targetClass, String source) {
			super(targetClass, targetClass, source);
			this.source = source;
		}

		private static String getSourceId(Class<?> cls) {
			ObjectTypeId id = cls.getAnnotation(ObjectTypeId.class);
			if (id != null) return id.value();
			Log.trace("Inline adapter %s has no ObjectTypeId annotation", cls);
			return cls.getName().toLowerCase();
		}

		@Override
		protected void configureJavaArguments(MethodDeclaration decl) {
			decl.declareJavaArgType(ARG_CONTEXT, ILuaContext.class);
		}

		@Override
		protected Callable<Object[]> createWrapper(MethodDeclaration decl, ILuaContext context, Object target, Object[] args) {
			return decl.createWrapper(target)
					.setJavaArg(ARG_CONTEXT, context)
					.setLuaArgs(args);
		}

		@Override
		public IObjectMethodExecutor createExecutor(FieldContext context) {
			return new ObjectPropertyExecutor(context);
		}

		@Override
		protected List<IObjectMethodExecutor> buildMethodList() {
			List<IObjectMethodExecutor> result = super.buildMethodList();
			PropertyListBuilder.buildPropertyList(targetClass, source, this, result);
			return result;
		}

		@Override
		public String describe() {
			return "internal object (source: " + adapterClass.toString() + ")";
		}
	}
}
