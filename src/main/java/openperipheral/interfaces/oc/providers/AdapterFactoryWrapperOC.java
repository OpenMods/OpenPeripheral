package openperipheral.interfaces.oc.providers;

import li.cil.oc.api.machine.Value;
import li.cil.oc.api.network.ManagedEnvironment;
import openperipheral.ApiImplementation;
import openperipheral.adapter.composed.ComposedMethodsFactory;
import openperipheral.api.adapter.GenerationFailedException;
import openperipheral.api.architecture.oc.IOpenComputersObjectsFactory;
import openperipheral.interfaces.oc.ModuleOpenComputers;

import com.google.common.base.Preconditions;

@ApiImplementation
public class AdapterFactoryWrapperOC implements IOpenComputersObjectsFactory {

	private static <T> T wrap(Object target, final ComposedMethodsFactory<IEnviromentInstanceWrapper<T>> factory) {
		Preconditions.checkNotNull(target, "Can't wrap null");
		IEnviromentInstanceWrapper<T> wrapper = factory.getAdaptedClass(target.getClass());
		return wrapper.isEmpty()? null : wrapper.createEnvironment(target);
	}

	@Override
	public Value wrapObject(Object target) {
		try {
			return wrap(target, ModuleOpenComputers.OBJECT_METHODS_FACTORY);
		} catch (Throwable t) {
			throw new GenerationFailedException(String.format("%s (%s)", target, target.getClass()), t);
		}
	}

	@Override
	public ManagedEnvironment createPeripheral(Object target) {
		try {
			return wrap(target, ModuleOpenComputers.PERIPHERAL_METHODS_FACTORY);
		} catch (Throwable t) {
			throw new GenerationFailedException(String.format("%s (%s)", target, target.getClass()), t);
		}
	}

}
