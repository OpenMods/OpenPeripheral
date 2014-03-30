package openperipheral.adapter.peripheral;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import openperipheral.adapter.composed.ClassMethodsList;
import dan200.computercraft.api.peripheral.IPeripheral;

public class ProxyAdapterPeripheral extends AdapterPeripheral implements InvocationHandler {

	public ProxyAdapterPeripheral(ClassMethodsList<IPeripheralMethodExecutor> wrapper, Object targetObject) {
		super(wrapper, targetObject);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getDeclaringClass() == IPeripheral.class) return method.invoke(this, args);
		return method.invoke(targetObject, args);
	}

}
