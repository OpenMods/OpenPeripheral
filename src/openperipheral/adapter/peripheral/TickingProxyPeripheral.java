package openperipheral.adapter.peripheral;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import openperipheral.adapter.composed.ClassMethodsList;
import openperipheral.api.IUpdateHandler;

import com.google.common.base.Throwables;

import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.IPeripheral;

public class TickingProxyPeripheral extends HostedPeripheralBase<IUpdateHandler> implements InvocationHandler {

	public TickingProxyPeripheral(ClassMethodsList<IPeripheralMethodExecutor> wrapper, IUpdateHandler targetObject) {
		super(wrapper, targetObject);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		final Class<?> declaringClass = method.getDeclaringClass();
		try {
			if (declaringClass.equals(IPeripheral.class) || declaringClass.equals(IHostedPeripheral.class)) return method.invoke(this, args);
			return method.invoke(targetObject, args);
		} catch (InvocationTargetException e) {
			Throwable wrapper = e.getCause();
			throw Throwables.propagate(wrapper != null? wrapper : e);
		}
	}

	@Override
	public void update() {
		targetObject.onPeripheralUpdate();
	}

}
