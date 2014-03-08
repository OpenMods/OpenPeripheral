package openperipheral.adapter.peripheral;

import openperipheral.adapter.AdaptedClass;

public class HostedPeripheral extends HostedPeripheralBase<Object> {

	public HostedPeripheral(AdaptedClass<IPeripheralMethodExecutor> wrapper, Object targetObject) {
		super(wrapper, targetObject);
	}

}
