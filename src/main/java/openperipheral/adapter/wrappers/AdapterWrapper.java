package openperipheral.adapter.wrappers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.lang.reflect.Method;
import java.util.List;
import openmods.Log;
import openperipheral.adapter.AnnotationMetaExtractor;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.method.MethodWrapperBuilder;
import openperipheral.api.adapter.method.ScriptCallable;

public abstract class AdapterWrapper {

	public static class MethodWrapException extends RuntimeException {
		private static final long serialVersionUID = -5116134133615320058L;

		public MethodWrapException(Method method, Throwable cause) {
			super(String.format("Failed to wrap method '%s'", method), cause);
		}
	}

	protected interface ExecutorFactory {
		public IMethodExecutor createExecutor(AnnotationMetaExtractor.Bound metaInfo, MethodWrapperBuilder decl);
	}

	protected final List<IMethodExecutor> methods;
	protected final Class<?> targetClass;
	protected final Class<?> adapterClass;
	protected final String source;

	protected AdapterWrapper(Class<?> adapterClass, Class<?> targetClass, Class<?> rootClass, String source, ExecutorFactory executorFactory) {
		this.adapterClass = adapterClass;
		this.targetClass = targetClass;
		this.source = source;

		final AnnotationMetaExtractor metaInfo = new AnnotationMetaExtractor(adapterClass);
		this.methods = ImmutableList.copyOf(buildMethodList(rootClass, metaInfo, executorFactory));
	}

	public String source() {
		return source;
	}

	public List<IMethodExecutor> getMethods() {
		return methods;
	}

	public Class<?> getAdapterClass() {
		return adapterClass;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public abstract boolean canUse(Class<?> cls);

	public abstract String describe();

	protected List<IMethodExecutor> buildMethodList(Class<?> rootClass, AnnotationMetaExtractor metaInfo, ExecutorFactory executorFactory) {
		List<IMethodExecutor> result = Lists.newArrayList();

		Method[] clsMethods;
		try {
			clsMethods = adapterClass.getDeclaredMethods();
		} catch (Throwable t) {
			// Trust no one. We got report about ClassNotFound from this place
			Log.severe(t, "Can't get adapter %s methods (possible sideness fail), bailing out", adapterClass);
			return result;
		}

		for (Method method : clsMethods) {
			try {
				ScriptCallable callableAnn = method.getAnnotation(ScriptCallable.class);
				if (callableAnn == null) continue;

				final MethodWrapperBuilder decl = new MethodWrapperBuilder(rootClass, method, callableAnn, source);
				final IMethodExecutor exec = executorFactory.createExecutor(metaInfo.forElement(method), decl);
				result.add(exec);
			} catch (Throwable e) {
				throw new MethodWrapException(method, e);
			}
		}

		return result;
	}
}