package openperipheral.adapter.peripheral;

import openperipheral.adapter.composed.ClassMethodsList;
import openperipheral.api.IUpdateHandler;

public class TickingHostedPeripheral extends HostedPeripheralBase<IUpdateHandler> {

	public TickingHostedPeripheral(ClassMethodsList<IPeripheralMethodExecutor> wrapper, IUpdateHandler targetObject) {
		super(wrapper, targetObject);
	}

	@Override
	public void update() {
		targetObject.onPeripheralUpdate();
	}
}
