package openperipheral.adapter.peripheral;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import openperipheral.adapter.composed.ClassMethodsList;

import com.google.common.base.Throwables;

import dan200.computer.api.IHostedPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;

public class ProxyPeripheral extends HostedPeripheralBase<Object> implements InvocationHandler {

	public ProxyPeripheral(ClassMethodsList<IPeripheralMethodExecutor> wrapper, Object targetObject) {
		super(wrapper, targetObject);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return forwardCall(this, targetObject, method, args);
	}

	static Object forwardCall(Object self, Object target, Method method, Object[] args) throws IllegalAccessException {
		final Class<?> declaringClass = method.getDeclaringClass();
		try {
			if (declaringClass.equals(IPeripheral.class) || declaringClass.equals(IHostedPeripheral.class)) return method.invoke(self, args);
			return method.invoke(target, args);
		} catch (InvocationTargetException e) {
			Throwable wrapper = e.getCause();
			throw Throwables.propagate(wrapper != null? wrapper : e);
		}
	}

}
