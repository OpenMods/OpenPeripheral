package openperipheral.adapter.peripheral;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import openperipheral.adapter.composed.ClassMethodsList;
import dan200.computer.api.IHostedPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;

public class ProxyPeripheral extends HostedPeripheralBase<Object> implements InvocationHandler {

	public ProxyPeripheral(ClassMethodsList<IPeripheralMethodExecutor> wrapper, Object targetObject) {
		super(wrapper, targetObject);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		final Class<?> declaringClass = method.getDeclaringClass();
		if (declaringClass == IPeripheral.class || declaringClass == IHostedPeripheral.class) return method.invoke(this, args);
		return method.invoke(targetObject, args);
	}

}
