package openperipheral.adapter.peripheral;

import openperipheral.adapter.AdaptedClass;
import openperipheral.api.IUpdateHandler;

public class TickingHostedPeripheral extends HostedPeripheralBase<IUpdateHandler> {

	public TickingHostedPeripheral(AdaptedClass<IPeripheralMethodExecutor> wrapper, IUpdateHandler targetObject) {
		super(wrapper, targetObject);
	}

	@Override
	public void update() {
		targetObject.onPeripheralUpdate();
	}
}
