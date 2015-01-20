package openperipheral.interfaces.oc.providers;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;

import li.cil.oc.api.network.ManagedEnvironment;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.WrappedEntityBase;
import openperipheral.api.ExposeInterface;
import openperipheral.interfaces.oc.ModuleOpenComputers;
import openperipheral.interfaces.oc.asm.EnvironmentFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public class EnvironmentProvider {

	private final EnvironmentFactory evnClassFactory = new EnvironmentFactory();

	private int clsCounter;

	private Map<Class<?>, Constructor<? extends ManagedEnvironment>> generatedClasses = Maps.newHashMap();

	public Constructor<? extends ManagedEnvironment> getEnvClass(Class<?> targetCls) {
		Constructor<? extends ManagedEnvironment> envCtor = generatedClasses.get(targetCls);

		if (envCtor == null) {
			Map<String, IMethodExecutor> methods = ModuleOpenComputers.PERIPHERAL_METHODS_FACTORY.getAdaptedClass(targetCls);

			ExposeInterface intfAnnotation = targetCls.getAnnotation(ExposeInterface.class);

			Set<Class<?>> exposedInterfaces = intfAnnotation != null? getInterfaces(targetCls, intfAnnotation.value()) : ImmutableSet.<Class<?>> of();

			String name = String.format("OpEnvironment$$%04d_%08X", clsCounter++, System.identityHashCode(targetCls));
			Class<? extends ManagedEnvironment> cls = evnClassFactory.generateEnvironment(name, targetCls, exposedInterfaces, new WrappedEntityBase(methods));

			try {
				envCtor = cls.getConstructor(targetCls);
			} catch (Exception e) {
				throw Throwables.propagate(e);
			}

			generatedClasses.put(targetCls, envCtor);
		}

		return envCtor;
	}

	private static Set<Class<?>> getInterfaces(Class<?> targetClass, Class<?>[] value) {
		Set<Class<?>> result = ImmutableSet.copyOf(value);

		for (Class<?> intf : result)
			Preconditions.checkArgument(intf.isAssignableFrom(targetClass), "Class %s tries to expose interface %s, but does not implement it");

		return result;
	}

	public ManagedEnvironment createEnvironment(Object target) {
		Constructor<? extends ManagedEnvironment> envCtor = getEnvClass(target.getClass());

		try {
			return envCtor.newInstance(target);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}
}
