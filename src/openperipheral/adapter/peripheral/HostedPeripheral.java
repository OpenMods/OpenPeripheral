package openperipheral.adapter.peripheral;

import openperipheral.adapter.composed.ClassMethodsList;

public class HostedPeripheral extends HostedPeripheralBase<Object> {

	public HostedPeripheral(ClassMethodsList<IPeripheralMethodExecutor> wrapper, Object targetObject) {
		super(wrapper, targetObject);
	}

}
