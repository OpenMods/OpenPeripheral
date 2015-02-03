package openperipheral.interfaces.oc.providers;

import li.cil.oc.api.machine.Value;
import li.cil.oc.api.network.ManagedEnvironment;
import openperipheral.ApiImplementation;
import openperipheral.api.architecture.oc.IOpenComputersObjectsFactory;
import openperipheral.interfaces.oc.wrappers.ManagedPeripheralWrapper;

@ApiImplementation
public class AdapterFactoryWrapperOC implements IOpenComputersObjectsFactory {

	@Override
	public Value wrapObject(Object target) {
		return ManagedPeripheralWrapper.wrap(target);
	}

	@Override
	public ManagedEnvironment createPeripheral(Object target) {
		return DriverOpenPeripheral.PROVIDER.createEnvironment(target);
	}

}
