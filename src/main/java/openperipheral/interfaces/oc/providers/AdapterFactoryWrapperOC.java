package openperipheral.interfaces.oc.providers;

import li.cil.oc.api.machine.Value;
import li.cil.oc.api.network.ManagedEnvironment;
import openperipheral.ApiImplementation;
import openperipheral.adapter.composed.ComposedMethodsFactory;
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
		return wrap(target, ModuleOpenComputers.OBJECT_METHODS_FACTORY);
	}

	@Override
	public ManagedEnvironment createPeripheral(Object target) {
		return wrap(target, ModuleOpenComputers.PERIPHERAL_METHODS_FACTORY);
	}

}
