package openperipheral.adapter.peripheral;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import openperipheral.adapter.composed.ClassMethodsList;
import openperipheral.api.IUpdateHandler;

public class TickingProxyPeripheral extends HostedPeripheralBase<IUpdateHandler> implements InvocationHandler {

	public TickingProxyPeripheral(ClassMethodsList<IPeripheralMethodExecutor> wrapper, IUpdateHandler targetObject) {
		super(wrapper, targetObject);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return ProxyPeripheral.forwardCall(this, targetObject, method, args);
	}

	@Override
	public void update() {
		targetObject.onPeripheralUpdate();
	}

}
