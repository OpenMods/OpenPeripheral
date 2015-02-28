package openperipheral.interfaces.oc.providers;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;

import openmods.injector.InjectedClassesManager;
import openperipheral.adapter.AdapterRegistry;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.composed.ComposedMethodsFactory;
import openperipheral.adapter.composed.IndexedMethodMap;
import openperipheral.api.peripheral.ExposeInterface;
import openperipheral.interfaces.oc.asm.ICodeGenerator;
import openperipheral.interfaces.oc.asm.MethodsStore;
import openperipheral.util.NameUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;

public class EnvironmentMethodsFactory<T> extends ComposedMethodsFactory<IEnviromentInstanceWrapper<T>> {

	private static class Wrapper<T> implements IEnviromentInstanceWrapper<T> {
		private final String generatedClsName;
		private final byte[] bytes;
		private final Class<?> targetCls;
		private final Map<String, IMethodExecutor> methods;

		private Constructor<? extends T> ctor;

		public Wrapper(String generatedClsName, byte[] bytes, Class<?> targetCls, Map<String, IMethodExecutor> methods) {
			this.generatedClsName = generatedClsName;
			this.bytes = bytes;
			this.targetCls = targetCls;
			this.methods = methods;
		}

		private Constructor<? extends T> getConstructor() {
			if (ctor == null) {
				try {
					@SuppressWarnings("unchecked")
					Class<? extends T> cls = (Class<? extends T>)Class.forName(generatedClsName);
					ctor = cls.getConstructor(targetCls);
				} catch (Throwable t) {
					throw Throwables.propagate(t);
				}
			}

			return ctor;
		}

		@Override
		public T createEnvironment(Object target) {
			try {
				return getConstructor().newInstance(target);
			} catch (Exception e) {
				throw Throwables.propagate(e);
			}
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public int size() {
			return methods.size();
		}

		@Override
		public void visitMethods(IMethodVisitor visitor) {
			for (Map.Entry<String, IMethodExecutor> e : methods.entrySet())
				visitor.visit(e.getKey(), e.getValue());
		}

		@Override
		public byte[] getClassBytes() {
			return bytes;
		}
	}

	private final IEnviromentInstanceWrapper<T> DUMMY = new IEnviromentInstanceWrapper<T>() {
		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public T createEnvironment(Object target) {
			return null;
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public void visitMethods(IMethodVisitor visitor) {}

		@Override
		public byte[] getClassBytes() {
			return null;
		}
	};

	private final ICodeGenerator generator;

	private final String classProviderId;

	public EnvironmentMethodsFactory(AdapterRegistry adapters, Predicate<IMethodExecutor> selector, String classProviderId, ICodeGenerator generator) {
		super(adapters, selector);
		this.generator = generator;
		this.classProviderId = classProviderId;
	}

	@Override
	protected IEnviromentInstanceWrapper<T> wrapMethods(Class<?> targetCls, Map<String, IMethodExecutor> methods) {
		if (methods.isEmpty()) return DUMMY;

		IndexedMethodMap methodMap = new IndexedMethodMap(methods);

		ExposeInterface intfAnnotation = targetCls.getAnnotation(ExposeInterface.class);

		Set<Class<?>> exposedInterfaces = intfAnnotation != null? getInterfaces(targetCls, intfAnnotation.value()) : ImmutableSet.<Class<?>> of();

		String obfTargetClass = NameUtils.grumize(targetCls);

		String generatedClassName = InjectedClassesManager.instance.createClassName(classProviderId, obfTargetClass);

		int methodsId = MethodsStore.drop(methodMap.getMethods());

		byte[] bytes = generator.generate(generatedClassName, targetCls, exposedInterfaces, methodMap, methodsId);

		return new Wrapper<T>(generatedClassName, bytes, targetCls, methods);

	}

	private static Set<Class<?>> getInterfaces(Class<?> targetClass, Class<?>[] value) {
		Set<Class<?>> result = ImmutableSet.copyOf(value);

		for (Class<?> intf : result)
			Preconditions.checkArgument(intf.isAssignableFrom(targetClass), "Class %s tries to expose interface %s, but does not implement it");

		return result;
	}
}
